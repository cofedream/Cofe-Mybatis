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
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.util.PsiTreeUtil;
import tk.cofe.plugin.mybatis.annotation.Annotation;

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
        PsiModifierList modifierList = target.getModifierList();
        return modifierList != null && modifierList.hasAnnotation(annotation.getQualifiedName());
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

    // 字段相关

    /**
     * 判断是否为目标字段
     *
     * @param psiField 字段
     */
    public static boolean notSerialField(PsiField psiField) {
        return !"serialVersionUID".equals(psiField.getName());
    }

    // 方法相关

    /**
     * 判断是否为 void 方法
     *
     * @param method 方法
     */
    public static boolean isVoidMethod(PsiMethod method) {
        return PsiTypeUtils.isVoid(method.getReturnType());
    }

    /**
     * 判断是否为 getXXX 函数
     *
     * @param method 方法
     */
    public static boolean isGetMethod(PsiMethod method) {
        return isPublicMethod(method)
                && !isVoidMethod(method)
                && method.getName().startsWith("get")
                && method.getName().length() > 3;
    }

    /**
     * 判断是否为 本地方法 函数
     *
     * @param method 方法
     */
    public static boolean isNativeMethod(PsiMethod method) {
        return hasModifierProperty(method, PsiModifier.NATIVE);
    }

    /**
     * 判断是否为 public 函数
     *
     * @param method 方法
     */
    public static boolean isPublicMethod(PsiMethod method) {
        return hasModifierProperty(method, PsiModifier.PUBLIC);
    }

    /**
     * 判断是否又指定标识符
     *
     * @param method   方法
     * @param modifier 标识符 {@link PsiModifier}
     */
    private static boolean hasModifierProperty(PsiMethod method, String modifier) {
        return method.getModifierList().hasModifierProperty(modifier);
    }

    /**
     * 获取指定方法
     *
     * @param psiClass   类信息
     * @param methodName 方法名称
     * @return 方法信息
     */
    public static Optional<PsiMethod> findPsiMethod(PsiClass psiClass, String methodName) {
        PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
        if (methods.length == 0) {
            return Optional.empty();
        }
        return Optional.of(methods[0]);
    }


    /**
     * 处理 getAaaBbb 方法名称
     *
     * @param method java方法
     * @return getAaaBbb->aaaBbb,或者 ""
     */

    public static String replaceGetPrefix(PsiMethod method) {
        String methodName = method.getName();
        if (methodName.length() == 3) {
            return "";
        }
        char first = Character.toLowerCase(methodName.charAt(3));
        if (methodName.length() > 4) {
            return first + methodName.substring(4);
        }
        return String.valueOf(first);
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

    /**
     * 转为 getXxx方法名
     */

    public static String toGetPrefix(final String text) {
        return "get" + Character.toUpperCase(text.charAt(0)) + (text.length() > 1 ? text.substring(1) : "");
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
}
