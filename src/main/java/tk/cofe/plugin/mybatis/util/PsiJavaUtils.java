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

package tk.cofe.plugin.mybatis.util;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;

import java.util.Collection;

/**
 * JavaPsi工具
 * @author : zhengrf
 * @date : 2019-01-01
 */
public final class PsiJavaUtils {

    // 注解相关

    /**
     * 判断是否有指定注解
     * @param target     目标元素
     * @param annotation 目标注解
     * @return true 有指定注解，false 没有指定注解
     */
    public static boolean hasAnnotation(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        PsiModifierList modifierList = target.getModifierList();
        return modifierList != null && modifierList.hasAnnotation(annotation.getQualifiedName());
    }

    /**
     * 判断是否有指定注解
     * @param target      目标元素
     * @param annotations 目标注解集合
     * @return true 有指定注解，false 没有指定注解
     */
    public static boolean hasAnnotations(@NotNull PsiModifierListOwner target, @NotNull Collection<Annotation> annotations) {
        return annotations.stream().anyMatch(annotation -> hasAnnotation(target, annotation));
    }

    // 注解相关

    // 包相关

    /**
     * 判断是否已经导入目标类
     * @param file          Java类文件
     * @param qualifiedName 类全限定名
     * @return true 已导入，false 未导入
     */
    public static boolean hasImportClass(@NotNull PsiJavaFile file, @NotNull String qualifiedName) {
        PsiImportList importList = file.getImportList();
        if (importList == null) {
            return false;
        }
        return importList.findSingleClassImportStatement(qualifiedName) != null;
    }

    /**
     * 导入类到指定Java文件
     * @param file     Java文件
     * @param psiClass 要导入的类
     */
    public static void importClass(@NotNull PsiJavaFile file, @NotNull PsiClass psiClass) {
        file.importClass(psiClass);
    }
    // 包相关

    // Java 相关Element

    /**
     * 判断 PsiElement 是否为接口
     * @param psiElement 元素
     * @return 如果是接口则返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isInterface(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiClass && ((PsiClass) psiElement).isInterface();
    }

    /**
     * 判断 PsiElement 是否为类方法
     * @param psiElement 元素
     * @return 如果是接口则返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isInterfaceMethod(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiMethod && isElementWithinInterface(psiElement);
    }

    private static boolean isElementWithinInterface(@Nullable PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass parentOfType = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return parentOfType != null && parentOfType.isInterface();
    }

    /**
     * 判断是否为 void 方法
     * @param method 方法
     */
    public static boolean isVoidMethod(@NotNull PsiMethod method) {
        return PsiTypeUtils.isVoid(method.getReturnType());
    }

    /**
     * 判断是否为 getXXX 函数
     * @param method 方法
     */
    public static boolean isGetMethod(@NotNull PsiMethod method) {
        return method.getName().startsWith("get") && method.getName().length() > 3;
    }

    /**
     * 判断是否为 本地方法 函数
     * @param method 方法
     */
    public static boolean isNativeMethod(@NotNull PsiMethod method) {
        return hasModifierProperty(method, PsiModifier.NATIVE);
    }

    /**
     * 判断是否为 public 函数
     * @param method 方法
     */
    public static boolean isPublicMethod(@NotNull PsiMethod method) {
        return hasModifierProperty(method, PsiModifier.PUBLIC);
    }

    /**
     * 判断是否又指定标识符
     * @param method   方法
     * @param modifier 标识符 {@link PsiModifier}
     */
    private static boolean hasModifierProperty(@NotNull PsiMethod method, String modifier) {
        return method.getModifierList().hasModifierProperty(modifier);
    }

}
