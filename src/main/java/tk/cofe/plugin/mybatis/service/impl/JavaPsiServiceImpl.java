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

package tk.cofe.plugin.mybatis.service.impl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public class JavaPsiServiceImpl implements JavaPsiService {
    private final Project project;
    private final JavaPsiFacade javaPsiFacade;

    public JavaPsiServiceImpl(Project project) {
        this.project = project;
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
    }

    @Override
    public void importClass(PsiJavaFile file, String qualifiedName) {
        if (!PsiJavaUtils.hasImportClass(file, qualifiedName)) {
            findPsiClass(qualifiedName).ifPresent(psiClass -> PsiJavaUtils.importClass(file, psiClass));
        }
    }

    @Override
    public void addAnnotation(@NotNull PsiModifierListOwner psiModifierListOwner, @NotNull Annotation annotation) {
        PsiModifierList modifierList = psiModifierListOwner.getModifierList();
        if (modifierList != null) {
            this.importClass((PsiJavaFile) psiModifierListOwner.getContainingFile(), annotation.getQualifiedName());
            modifierList.add(javaPsiFacade.getElementFactory().createAnnotationFromText(annotation.toString(), psiModifierListOwner));
        }
    }

    @NotNull
    @Override
    public Optional<PsiClass> findPsiClass(@NotNull String qualifiedName) {
        PsiClass aClass = javaPsiFacade.findClass(qualifiedName, GlobalSearchScope.projectScope(project));
        return Optional.ofNullable(aClass == null ? javaPsiFacade.findClass(qualifiedName, GlobalSearchScope.allScope(project)) : aClass);
    }

    @NotNull
    @Override
    public Optional<PsiMethod> findPsiMethod(@Nullable final String qualifiedName, @Nullable final String methodName) {
        if (StringUtil.isEmpty(qualifiedName) || StringUtil.isEmpty(methodName)) {
            return Optional.empty();
        }
        return findPsiClass(qualifiedName).flatMap(psiClass -> {
            PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
            if (methods.length == 0) {
                return Optional.empty();
            }
            return Optional.of(methods[0]);
        });
    }

}
