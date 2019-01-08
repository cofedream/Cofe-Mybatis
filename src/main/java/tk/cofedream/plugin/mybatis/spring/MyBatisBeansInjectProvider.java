package tk.cofedream.plugin.mybatis.spring;

import com.intellij.ide.projectView.impl.ProjectRootsUtil;
import com.intellij.lang.jvm.annotation.JvmAnnotationArrayValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationClassValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
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
import com.intellij.util.containers.hash.HashSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : zhengrf
 * @date : 2018-12-31
 */
public class MyBatisBeansInjectProvider extends SpringMyBatisBeansProvider {
    @NonNls
    private static final String TK_MAPPER_FACTORY_BEAN = "tk.mybatis.spring.mapper.MapperFactoryBean";
    private static final String TK_MAPPER_SCANNER_CONFIGURER = "tk.mybatis.spring.mapper.MapperScannerConfigurer";
    /**
     * tk.mybatis 的接口
     */
    private static final String TK_MAPPER_SCAN = "tk.mybatis.spring.annotation.MapperScan";
    /**
     * 原生mybatis的接口
     */
    private static final String ORG_MAPPER_SCAN = "org.mybatis.spring.annotation.MapperScan";

    private static void processBasePackage(@NotNull GlobalSearchScope scope, @NotNull PsiPackage aPackage, @NotNull Collection<CommonSpringBean> myBatisMappers) {
        PsiClass[] psiClasses = aPackage.getClasses(scope);
        for (PsiClass aClass : psiClasses) {
            if (aClass.isInterface()) {
                myBatisMappers.add(new CustomSpringComponent(aClass));
            }
        }
        PsiPackage[] subPackages = aPackage.getSubPackages(scope);
        for (PsiPackage psiPackage : subPackages) {
            processBasePackage(scope, psiPackage, myBatisMappers);
        }

    }

    @NotNull
    @Override
    public Collection<CommonSpringBean> getCustomComponents(@NotNull LocalModel springModel) {
        Module module = springModel.getModule();
        if (module != null && !DumbService.isDumb(module.getProject())) {
            Collection<CommonSpringBean> myBatisMappers = new HashSet<>();
            if (springModel instanceof LocalXmlModel) {
                super.collectMappers((LocalXmlModel) springModel, module, myBatisMappers, TK_MAPPER_FACTORY_BEAN);
                super.collectMappers((LocalXmlModel) springModel, module, myBatisMappers, TK_MAPPER_SCANNER_CONFIGURER);
            } else if (springModel instanceof LocalAnnotationModel) {
                this.collectMappers((LocalAnnotationModel) springModel, module, myBatisMappers, TK_MAPPER_SCAN);
                this.collectMappers((LocalAnnotationModel) springModel, module, myBatisMappers, ORG_MAPPER_SCAN);
            }
            return myBatisMappers;
        } else {
            return Collections.emptyList();
        }
    }

    private void collectMappers(@NotNull LocalAnnotationModel springModel, Module module, Collection<CommonSpringBean> myBatisMappers, String annotationClassName) {
        PsiClass libraryClass = SpringCommonUtils.findLibraryClass(module, annotationClassName);
        if (libraryClass != null) {
            PsiClass config = springModel.getConfig();
            PsiAnnotation annotation = config.getAnnotation(annotationClassName);
            if (annotation != null) {
                GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, ProjectRootsUtil.isInTestSource(config.getContainingFile()));
                JavaPsiFacade facade = JavaPsiFacade.getInstance(config.getProject());
                annotation.getAttributes().forEach(attribute -> {
                    JvmAnnotationAttributeValue attributeValue = attribute.getAttributeValue();
                    if (attributeValue != null) {
                        String attributeName = attribute.getAttributeName();
                        PsiClass psiClass;
                        switch (attributeName) {
                            case "value":
                            case "basePackages":
                                if (attributeValue instanceof JvmAnnotationConstantValue) {
                                    PsiPackage psiPackage = getPsiPackage(facade, ((JvmAnnotationConstantValue) attributeValue));
                                    if (psiPackage != null) {
                                        processBasePackage(scope, psiPackage, myBatisMappers);
                                    }
                                } else if (attributeValue instanceof JvmAnnotationArrayValue) {
                                    getPsiPackage(facade, (JvmAnnotationArrayValue) attributeValue).forEach(psiPackage -> processBasePackage(scope, psiPackage, myBatisMappers));
                                }
                                break;
                            case "basePackageClasses":
                                psiClass = (PsiClass) ((JvmAnnotationClassValue) attributeValue).getClazz();
                                if (psiClass != null) {
                                    String classQualifiedName = psiClass.getQualifiedName();
                                    processBasePackage(scope, facade.findPackage(classQualifiedName.substring(0, classQualifiedName.lastIndexOf("."))), myBatisMappers);
                                }
                                break;
                            case "annotationClass":
                                psiClass = (PsiClass) ((JvmAnnotationClassValue) attributeValue).getClazz();
                                if (psiClass != null) {
                                    ClassesWithAnnotatedMembersSearch.search(psiClass, GlobalSearchScope.projectScope(config.getProject())).findAll().forEach(aClass -> {
                                        if (aClass.isInterface()) {
                                            myBatisMappers.add(new CustomSpringComponent(aClass));
                                        }
                                    });
                                }
                                break;
                            case "markerInterface":
                                psiClass = (PsiClass) ((JvmAnnotationClassValue) attributeValue).getClazz();
                                if (psiClass != null) {
                                    ClassInheritorsSearch.search(psiClass).findAll().forEach(aClass -> {
                                        if (aClass.isInterface()) {
                                            myBatisMappers.add(new CustomSpringComponent(aClass));
                                        }
                                    });
                                }
                            default:
                                break;
                        }
                    }
                });
            }
        }
    }

    @Nullable
    private PsiPackage getPsiPackage(JavaPsiFacade facade, JvmAnnotationConstantValue attributeValue) {
        Object constantValue = attributeValue.getConstantValue();
        if (constantValue != null) {
            return facade.findPackage(constantValue.toString().trim());
        }
        return null;
    }

    @NotNull
    private List<PsiPackage> getPsiPackage(JavaPsiFacade facade, JvmAnnotationArrayValue attributeValue) {
        return new LinkedList<PsiPackage>() {{
            attributeValue.getValues().forEach(attr -> this.add(facade.findPackage(attr.toString().trim())));
        }};
    }
}

