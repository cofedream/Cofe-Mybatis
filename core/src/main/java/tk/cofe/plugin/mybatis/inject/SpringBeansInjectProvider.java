/*
 * Copyright (C) 2019 cofe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mybatis.inject;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiArrayInitializerMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopeUtil;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.AnnotationTargetsSearch;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.search.searches.ClassesWithAnnotatedMembersSearch;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.spring.contexts.model.LocalAnnotationModel;
import com.intellij.spring.contexts.model.LocalModel;
import com.intellij.spring.contexts.model.LocalXmlModel;
import com.intellij.spring.model.CommonSpringBean;
import com.intellij.spring.model.extensions.myBatis.SpringMyBatisBeansProvider;
import com.intellij.spring.model.jam.stereotype.CustomSpringComponent;
import com.intellij.spring.model.utils.SpringCommonUtils;
import com.intellij.util.Query;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.service.JavaPsiService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author : zhengrf
 * @date : 2018-12-31
 */
public class SpringBeansInjectProvider extends SpringMyBatisBeansProvider {
    // tk 相关类
    private static final String TK_MAPPER_FACTORY_BEAN = "tk.mybatis.spring.mapper.MapperFactoryBean";
    private static final String TK_MAPPER_SCANNER_CONFIGURER = "tk.mybatis.spring.mapper.MapperScannerConfigurer";
    private static final String TK_MAPPER_SCAN = "tk.mybatis.spring.annotation.MapperScan";
    // 原生mybatis的接口
    private static final String ORG_MAPPER_SCAN = "org.mybatis.spring.annotation.MapperScan";
    private final ConcurrentMap<String, Pattern> PACKAGE_PATTERN = ContainerUtil.createConcurrentSoftValueMap();

    @NotNull
    @Override
    public Collection<CommonSpringBean> getCustomComponents(LocalModel springModel) {
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

    private void collectMappers(LocalAnnotationModel springModel, Module module, Collection<CommonSpringBean> mappers, String annotationClassName) {
        if (SpringCommonUtils.findLibraryClass(module, annotationClassName) == null) {
            return;
        }
        PsiClass config = springModel.getConfig();
        PsiAnnotation annotation = config.getAnnotation(annotationClassName);
        if (annotation == null) {
            return;
        }
        GlobalSearchScope scope = GlobalSearchScope.projectScope(module.getProject());
        JavaPsiFacade facade = JavaPsiFacade.getInstance(config.getProject());
        final JavaPsiService psiService = JavaPsiService.getInstance(config.getProject());
        Arrays.stream(annotation.getParameterList().getAttributes())
                .filter(info -> info.getAttributeValue() != null)
                .flatMap(attribute -> {
                    final PsiAnnotationMemberValue attributeValue = attribute.getValue();
                    switch (attribute.getAttributeName()) {
                        case "value":
                        case "basePackages":
                            return annotation(facade, attributeValue).flatMap(psiPackage -> scanPackage(scope, psiPackage));
                        case "basePackageClasses":
                            return getPsiClass(attributeValue, psiClass -> scanPackage(scope, psiService.getPsiPackage(psiClass)));
                        case "annotationClass":
                            return getPsiClass(attributeValue, psiClass -> queryPsiClass(ClassesWithAnnotatedMembersSearch.search(psiClass, scope)));
                        case "markerInterface":
                            return getPsiClass(attributeValue, psiClass -> queryPsiClass(ClassInheritorsSearch.search(psiClass)));
                        default:
                            return Stream.empty();
                    }
                }).forEach(psiClass -> mappers.add(new CustomSpringComponent(psiClass)));
    }

    private static Stream<PsiClass> scanPackage(GlobalSearchScope scope, @Nullable PsiPackage psiPackage) {
        if (psiPackage == null) {
            return Stream.empty();
        }
        return Stream.concat(Arrays.stream(psiPackage.getClasses(scope)).filter(PsiClass::isInterface),
                Arrays.stream(psiPackage.getSubPackages(scope)).flatMap(info -> scanPackage(scope, info)));
    }

    private Stream<PsiClass> getPsiClass(final PsiAnnotationMemberValue value, final Function<PsiClass, Stream<PsiClass>> scan) {
        if (value instanceof PsiClassObjectAccessExpression) {
            final PsiTypeElement operand = ((PsiClassObjectAccessExpression) value).getOperand();
            final PsiClass psiClass = PsiTypesUtil.getPsiClass(operand.getType());
            return psiClass == null ? Stream.empty() : scan.apply(psiClass);
        }
        return Stream.empty();
    }

    private Stream<PsiClass> queryPsiClass(Query<PsiClass> search) {
        return search.findAll().stream().filter(PsiClass::isInterface);
    }

    @NotNull
    private Stream<PsiPackage> annotation(final JavaPsiFacade facade, final PsiAnnotationMemberValue value) {
        if (value == null) {
            return Stream.empty();
        }
        if (value instanceof PsiArrayInitializerMemberValue) {
            return AnnotationUtil.arrayAttributeValues(value).stream().flatMap(info -> getPsiPackage(facade, info));
        } else {
            return getPsiPackage(facade, value);
        }
    }

    @NotNull
    private Stream<PsiPackage> getPsiPackage(final JavaPsiFacade facade, @NotNull final PsiAnnotationMemberValue info) {
        if (info instanceof PsiClassObjectAccessExpression) {
            final PsiClass psiClass = PsiTypesUtil.getPsiClass(((PsiClassObjectAccessExpression) info).getOperand().getType());
            if (psiClass != null) {
                return getPsiPackage(facade, psiClass.getQualifiedName());
            }
        }
        return getPsiPackage(facade, info.getText().replaceAll("\"", ""));
    }

    private Stream<PsiPackage> getPsiPackage(JavaPsiFacade facade, @Nullable String qualifiedName) {
        if (StringUtil.isEmpty(qualifiedName)) {
            return Stream.empty();
        }
        if (qualifiedName.contains("*")) {
            PACKAGE_PATTERN.computeIfAbsent(qualifiedName, k -> Pattern.compile(qualifiedName
                    .replaceAll("\\.", "\\\\.")
                    .replaceAll("\\*\\*", ".*?")
                    .replaceAll("\\*", "[^.]+") + ".*"));
            return getLeafPsiPackage(facade.findPackage(qualifiedName.contains(".*") ? qualifiedName.substring(0, qualifiedName.indexOf(".*")) : qualifiedName))
                    .filter(psiPackage -> PACKAGE_PATTERN.get(qualifiedName).matcher(psiPackage.getQualifiedName()).matches());
        } else {
            PsiPackage psiPackage = facade.findPackage(qualifiedName);
            if (psiPackage != null) {
                return Stream.of(psiPackage);
            }
        }
        return Stream.empty();
    }

    private Stream<PsiPackage> getLeafPsiPackage(@Nullable PsiPackage psiPackage) {
        if (psiPackage == null) {
            return Stream.empty();
        }
        PsiPackage[] subPackages = psiPackage.getSubPackages();
        if (subPackages.length != 0) {
            return Stream.concat(Arrays.stream(subPackages).flatMap(this::getLeafPsiPackage), Stream.of(psiPackage));
        } else {
            return Stream.of(psiPackage);
        }
    }
}