package tk.cofe.plugin.mybatis.spring;

import com.intellij.ide.projectView.impl.ProjectRootsUtil;
import com.intellij.lang.jvm.annotation.JvmAnnotationArrayValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationClassValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.search.searches.ClassesWithAnnotatedMembersSearch;
import com.intellij.spring.contexts.model.LocalAnnotationModel;
import com.intellij.spring.contexts.model.LocalModel;
import com.intellij.spring.contexts.model.LocalXmlModel;
import com.intellij.spring.model.CommonSpringBean;
import com.intellij.spring.model.extensions.myBatis.SpringMyBatisBeansProvider;
import com.intellij.spring.model.jam.stereotype.CustomSpringComponent;
import com.intellij.spring.model.utils.SpringCommonUtils;
import com.intellij.util.Query;
import com.intellij.util.containers.ContainerUtil;
import com.siyeh.ig.psiutils.ExpressionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2018-12-31
 */
public class BeansInjectProvider extends SpringMyBatisBeansProvider {
    private final ConcurrentMap<String, Pattern> PACKAGE_PATTERN = ContainerUtil.createConcurrentSoftValueMap();
    // tk 相关类
    private static final String TK_MAPPER_FACTORY_BEAN = "tk.mybatis.spring.mapper.MapperFactoryBean";
    private static final String TK_MAPPER_SCANNER_CONFIGURER = "tk.mybatis.spring.mapper.MapperScannerConfigurer";
    private static final String TK_MAPPER_SCAN = "tk.mybatis.spring.annotation.MapperScan";

    // 原生mybatis的接口
    private static final String ORG_MAPPER_SCAN = "org.mybatis.spring.annotation.MapperScan";

    @NotNull
    @Override
    public Collection<CommonSpringBean> getCustomComponents(@NotNull LocalModel springModel) {
        Module module = springModel.getModule();
        if (module != null && !DumbService.isDumb(module.getProject())) {
            Collection<CommonSpringBean> mappers = new LinkedList<>();
            if (springModel instanceof LocalXmlModel) {
                super.collectMappers((LocalXmlModel) springModel, module, mappers, TK_MAPPER_FACTORY_BEAN);
                super.collectMappers((LocalXmlModel) springModel, module, mappers, TK_MAPPER_SCANNER_CONFIGURER);
            } else if (springModel instanceof LocalAnnotationModel) {
                this.collectMappers((LocalAnnotationModel) springModel, module, mappers, TK_MAPPER_SCAN);
                this.collectMappers((LocalAnnotationModel) springModel, module, mappers, ORG_MAPPER_SCAN);
            }
            return mappers;
        }
        return Collections.emptyList();
    }

    private void collectMappers(@NotNull LocalAnnotationModel springModel, Module module, Collection<CommonSpringBean> mappers, String annotationClassName) {
        if (SpringCommonUtils.findLibraryClass(module, annotationClassName) == null) {
            return;
        }
        PsiClass config = springModel.getConfig();
        PsiAnnotation annotation = config.getAnnotation(annotationClassName);
        if (annotation == null) {
            return;
        }
        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, ProjectRootsUtil.isInTestSource(config.getContainingFile()));
        JavaPsiFacade facade = JavaPsiFacade.getInstance(config.getProject());
        for (JvmAnnotationAttribute attribute : annotation.getAttributes()) {
            JvmAnnotationAttributeValue attributeValue = attribute.getAttributeValue();
            if (attributeValue == null) {
                return;
            }
            PsiClass psiClass;
            switch (attribute.getAttributeName()) {
                case "value":
                case "basePackages":
                    if (attributeValue instanceof JvmAnnotationConstantValue) {
                        getPsiPackage(facade, ((JvmAnnotationConstantValue) attributeValue)).forEach(psiPackage -> processBasePackage(scope, psiPackage, mappers));
                    } else if (attributeValue instanceof JvmAnnotationArrayValue) {
                        PsiAnnotationMemberValue basePackages = annotation.findAttributeValue(attribute.getAttributeName());
                        if (basePackages != null) {
                            getPsiPackage(facade, basePackages).forEach(psiPackage -> processBasePackage(scope, psiPackage, mappers));
                        }
                    }
                    break;
                case "basePackageClasses":
                    psiClass = (PsiClass) ((JvmAnnotationClassValue) attributeValue).getClazz();
                    if (psiClass != null) {
                        String classQualifiedName = psiClass.getQualifiedName();
                        if (StringUtils.isBlank(classQualifiedName)) {
                            return;
                        }
                        processBasePackage(scope, facade.findPackage(classQualifiedName.substring(0, classQualifiedName.lastIndexOf("."))), mappers);
                    }
                    break;
                case "annotationClass":
                    psiClass = (PsiClass) ((JvmAnnotationClassValue) attributeValue).getClazz();
                    if (psiClass != null) {
                        processQueryPsiClass(ClassesWithAnnotatedMembersSearch.search(psiClass, GlobalSearchScope.projectScope(config.getProject())), mappers);
                    }
                    break;
                case "markerInterface":
                    psiClass = (PsiClass) ((JvmAnnotationClassValue) attributeValue).getClazz();
                    if (psiClass != null) {
                        processQueryPsiClass(ClassInheritorsSearch.search(psiClass), mappers);
                    }
                default:
                    break;
            }

        }
    }

    private static void processBasePackage(@NotNull GlobalSearchScope scope, @Nullable PsiPackage psiPackage, @NotNull Collection<CommonSpringBean> mappers) {
        if (psiPackage == null) {
            return;
        }
        for (PsiClass psiClass : psiPackage.getClasses(scope)) {
            if (psiClass.isInterface()) {
                mappers.add(new CustomSpringComponent(psiClass));
            }
        }
        for (PsiPackage subPackage : psiPackage.getSubPackages(scope)) {
            processBasePackage(scope, subPackage, mappers);
        }
    }

    private void processQueryPsiClass(@NotNull Query<PsiClass> search, @NotNull Collection<CommonSpringBean> mappers) {
        for (PsiClass aClass : search.findAll()) {
            if (aClass.isInterface()) {
                mappers.add(new CustomSpringComponent(aClass));
            }
        }
    }

    @NotNull
    private List<PsiPackage> getPsiPackage(@NotNull JavaPsiFacade facade, @NotNull JvmAnnotationConstantValue attributeValue) {
        Object value = attributeValue.getConstantValue();
        return value == null ? Collections.emptyList() : getPsiPackage(facade, value.toString());
    }

    @NotNull
    private List<PsiPackage> getPsiPackage(@NotNull JavaPsiFacade facade, @NotNull PsiAnnotationMemberValue annotationMemberValue) {
        List<PsiPackage> res = new LinkedList<>();
        for (PsiElement child : annotationMemberValue.getChildren()) {
            if (!(child instanceof PsiExpression)) {
                continue;
            }
            PsiLiteralExpression literal = ExpressionUtils.getLiteral(((PsiExpression) child));
            if (literal == null) {
                continue;
            }
            String text = literal.getText();
            if (text == null) {
                continue;
            }
            res.addAll(getPsiPackage(facade, text.replaceAll("\"", "")));
            //if (qualifiedName.contains("*")) {
            //    PACKAGE_PATTERN.computeIfAbsent(qualifiedName, k -> Pattern.compile(qualifiedName
            //            .replaceAll("\\.", "\\\\.")
            //            .replaceAll("\\*\\*", ".*?")
            //            .replaceAll("\\*", "[^.]+") + ".*"));
            //    getLeafPsiPackage(facade.findPackage(qualifiedName.substring(0, qualifiedName.indexOf(".*")))).forEach(psiPackage -> {
            //        if (PACKAGE_PATTERN.get(qualifiedName).matcher(psiPackage.getQualifiedName()).matches()) {
            //            res.add(psiPackage);
            //        }
            //    });
            //} else {
            //    PsiPackage psiPackage = facade.findPackage(qualifiedName);
            //    if (psiPackage != null) {
            //        res.add(psiPackage);
            //    }
            //}
        }
        return res;
    }

    private List<PsiPackage> getPsiPackage(@NotNull JavaPsiFacade facade, @NotNull String qualifiedName) {
        if (qualifiedName.contains("*")) {
            PACKAGE_PATTERN.computeIfAbsent(qualifiedName, k -> Pattern.compile(qualifiedName
                    .replaceAll("\\.", "\\\\.")
                    .replaceAll("\\*\\*", ".*?")
                    .replaceAll("\\*", "[^.]+") + ".*"));
            return getLeafPsiPackage(facade.findPackage(qualifiedName.substring(0, qualifiedName.indexOf(".*")))).stream()
                    .filter(psiPackage -> PACKAGE_PATTERN.get(qualifiedName).matcher(psiPackage.getQualifiedName()).matches())
                    .collect(Collectors.toList());
        } else {
            PsiPackage psiPackage = facade.findPackage(qualifiedName);
            if (psiPackage != null) {
                return Collections.singletonList(psiPackage);
            }
        }
        return Collections.emptyList();
    }

    private List<PsiPackage> getLeafPsiPackage(@Nullable PsiPackage psiPackage) {
        if (psiPackage == null) {
            return Collections.emptyList();
        }
        List<PsiPackage> psiPackages = new ArrayList<>();
        PsiPackage[] subPackages = psiPackage.getSubPackages();
        if (subPackages.length != 0) {
            for (PsiPackage subPackage : subPackages) {
                psiPackages.addAll(getLeafPsiPackage(subPackage));
            }
        } else {
            psiPackages.add(psiPackage);
        }
        return psiPackages;
    }
}

