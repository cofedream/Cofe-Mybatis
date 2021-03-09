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

package tk.cofe.plugin.common.utils;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.annotation.Annotation;

import javax.annotation.ParametersAreNullableByDefault;
import javax.swing.*;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * JavaPsi工具
 *
 * @author : zhengrf
 * @date : 2019-01-01
 */
public final class PsiJavaUtils {

    // 注解相关

    /**
     * 判断是否有指定注解
     *
     * @param target     目标元素
     * @param annotation 目标注解
     * @return true 有指定注解，false 没有指定注解
     */
    public static boolean hasAnnotation(PsiModifierListOwner target, Annotation annotation) {
        return target.hasAnnotation(annotation.getQualifiedName());
    }

    /**
     * 判断是否有指定注解
     *
     * @param target      目标元素
     * @param annotations 目标注解集合
     * @return true 有指定注解，false 没有指定注解
     */
    public static boolean hasAnnotations(PsiModifierListOwner target, Collection<Annotation> annotations) {
        return annotations.stream().anyMatch(annotation -> hasAnnotation(target, annotation));
    }

    // 注解相关

    // 包相关

    /**
     * 判断是否已经导入目标类
     *
     * @param file          Java类文件
     * @param qualifiedName 类全限定名
     * @return true 已导入，false 未导入
     */
    public static boolean hasImportClass(PsiJavaFile file, String qualifiedName) {
        PsiImportList importList = file.getImportList();
        if (importList == null) {
            return false;
        }
        return importList.findSingleClassImportStatement(qualifiedName) != null;
    }

    /**
     * 导入类到指定Java文件
     *
     * @param file     Java文件
     * @param psiClass 要导入的类
     */
    public static void importClass(PsiJavaFile file, PsiClass psiClass) {
        file.importClass(psiClass);
    }

    // Java 相关Element

    /**
     * 判断 PsiElement 是否为接口
     *
     * @param psiElement 元素
     * @return 如果是接口则返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isInterface(PsiElement psiElement) {
        return psiElement instanceof PsiClass && ((PsiClass) psiElement).isInterface();
    }

    /**
     * 判断是否为 {@link Object}
     */
    public static boolean isObjectClass(PsiClass psiClass) {
        return Optional.ofNullable(psiClass)
                .map(PsiClass::getQualifiedName)
                .map("java.lang.Object"::equals)
                .orElse(false);
    }

    /**
     * 判断 PsiElement 是否为类方法
     *
     * @param psiElement 元素
     * @return 如果是接口则返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isInterfaceMethod(PsiElement psiElement) {
        return psiElement instanceof PsiMethod && isElementWithinInterface(psiElement);
    }

    private static boolean isElementWithinInterface(PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass parentOfType = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return parentOfType != null && parentOfType.isInterface();
    }

    public static void psiClassProcessor(PsiClassType psiClassType,
                                         Predicate<PsiField> fieldCondition, Consumer<PsiField> fieldConsumer,
                                         Predicate<PsiMethod> methodCondition, Consumer<PsiMethod> methodConsumer) {
        if (psiClassType == null) {
            return;
        }
        PsiClass psiClass = psiClassType.resolve();
        psiClassProcessor(psiClass, fieldCondition, fieldConsumer, methodCondition, methodConsumer);
    }

    public static void psiClassProcessor(PsiClass psiClass,
                                         Predicate<PsiField> fieldCondition, Consumer<PsiField> fieldConsumer,
                                         Predicate<PsiMethod> methodCondition, Consumer<PsiMethod> methodConsumer) {
        if (psiClass == null) {
            return;
        }
        for (PsiField field : psiClass.getAllFields()) {
            if (fieldCondition.test(field)) {
                fieldConsumer.accept(field);
            }
        }
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (methodCondition.test(method)) {
                methodConsumer.accept(method);
            }
        }
    }

    @Nullable
    public static Icon getPsiMemberIcon(@Nullable PsiMember psiMember) {
        if (psiMember == null) {
            return null;
        }
        if (psiMember instanceof PsiMethod) {
            return PlatformIcons.METHOD_ICON;
        }
        if (psiMember instanceof PsiField) {
            return PlatformIcons.FIELD_ICON;
        }
        return null;
    }

    @Nullable
    public static PsiType getPsiMemberType(@Nullable PsiMember psiMember) {
        if (psiMember == null) {
            return null;
        }
        if (psiMember instanceof PsiMethod) {
            return ((PsiMethod) psiMember).getReturnType();
        }
        if (psiMember instanceof PsiField) {
            return ((PsiField) psiMember).getType();
        }
        return null;
    }
}
