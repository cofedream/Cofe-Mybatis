/*
 * Copyright (C) 2019-2021 cofe
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

package tk.cofe.plugin.mybatis.service.impl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.PsiJavaUtils;
import tk.cofe.plugin.mybatis.service.JavaPsiService;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public class JavaPsiServiceImpl implements JavaPsiService {
    private final Project project;
    private final JavaPsiFacade facade;

    public JavaPsiServiceImpl(Project project) {
        this.project = project;
        this.facade = JavaPsiFacade.getInstance(project);
    }

    @Override
    public void importClass(PsiJavaFile file, String qualifiedName) {
        if (!PsiJavaUtils.hasImportClass(file, qualifiedName)) {
            findPsiClass(qualifiedName).ifPresent(psiClass -> PsiJavaUtils.importClass(file, psiClass));
        }
    }

    @Override
    public void addAnnotation(PsiModifierListOwner psiModifierListOwner, Annotation annotation) {
        if (psiModifierListOwner == null || annotation == null) {
            return;
        }
        PsiModifierList modifierList = psiModifierListOwner.getModifierList();
        if (modifierList != null) {
            importClass((PsiJavaFile) psiModifierListOwner.getContainingFile(), annotation.getQualifiedName());
            modifierList.add(facade.getElementFactory().createAnnotationFromText(annotation.toString(), psiModifierListOwner));
        }
    }

    @Override
    public Optional<PsiClass> findPsiClass(String qualifiedName) {
        if (qualifiedName == null) {
            return Optional.empty();
        }
        PsiClass psiClass = facade.findClass(qualifiedName, GlobalSearchScope.projectScope(project)); // 先搜索项目级别
        if (psiClass == null) {
            psiClass = facade.findClass(qualifiedName, GlobalSearchScope.allScope(project)); // 在搜索全部级别
        }
        return Optional.ofNullable(psiClass);
    }

    @Override
    public PsiPackage getPsiPackage(@Nullable final PsiClass psiClass) {
        return Optional.ofNullable(psiClass)
                .map(PsiClass::getQualifiedName)
                .filter(StringUtil::isNotEmpty)
                .map(qualifiedName -> facade.findPackage(qualifiedName.substring(0, qualifiedName.lastIndexOf("."))))
                .orElse(null);
    }

    @Override
    public Optional<PsiMethod> findPsiMethod(final String qualifiedName, final String methodName) {
        if (StringUtil.isEmpty(qualifiedName) || StringUtil.isEmpty(methodName)) {
            return Optional.empty();
        }
        return findPsiClass(qualifiedName)
                .map(psiClass -> psiClass.findMethodsByName(methodName, true))
                .filter(methods -> !ArrayUtil.isEmpty(methods))
                .map(methods -> methods[0]);
    }

}
