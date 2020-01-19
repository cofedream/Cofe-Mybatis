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

package tk.cofe.plugin.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-07
 */
public interface JavaPsiService {
    static JavaPsiService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, JavaPsiService.class);
    }

    PsiPackage getPsiPackage(@Nullable PsiClass psiClass);

    Optional<PsiMethod> findPsiMethod(String qualifiedName, String methodName);

    /**
     * 查找PsiClass
     *
     * @param qualifiedName 类全限定名
     * @return PsiClass
     */
    Optional<PsiClass> findPsiClass(String qualifiedName);

    /**
     * 导包
     *
     * @param file          Java类文件
     * @param qualifiedName 包名
     */
    void importClass(PsiJavaFile file, String qualifiedName);

    /**
     * 给元素添加注解
     *
     * @param psiModifierListOwner 目标元素
     * @param annotation           注解
     */
    void addAnnotation(PsiModifierListOwner psiModifierListOwner, Annotation annotation);

}
