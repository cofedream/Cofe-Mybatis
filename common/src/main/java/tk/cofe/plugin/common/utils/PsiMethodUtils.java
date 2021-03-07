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

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author : zhengrf
 * @date : 2021-03-07
 */
public class PsiMethodUtils {

    private static final Pattern START_GET = Pattern.compile("get[A-Z].*");
    private static final Pattern START_SET = Pattern.compile("set[A-Z].*");
    private static final Pattern START_IS = Pattern.compile("is[A-Z].*");

    public static boolean nameStartWithGet(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return false;
        }
        return START_GET.matcher(psiMethod.getName()).matches();
    }

    public static boolean nameStartWithIs(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return false;
        }
        return START_IS.matcher(psiMethod.getName()).matches();
    }

    /**
     * 处理 getAaaBbb 方法名称
     *
     * @param method java方法
     * @return getAaaBbb->aaaBbb,或者 ""
     */
    public static String replaceGetPrefix(PsiMethod method) {
        if (method == null) {
            return "";
        }
        String methodName = method.getName();
        if (!nameStartWithGet(method)) {
            return methodName;
        }
        char first = Character.toLowerCase(methodName.charAt(3));
        if (methodName.length() > 4) {
            return first + methodName.substring(4);
        }
        return String.valueOf(first);
    }

    /**
     * 处理 isAaaBbb 方法名称
     *
     * @param method java方法
     * @return isAaaBbb->aaaBbb,或者 ""
     */
    public static String replaceIsPrefix(PsiMethod method) {
        if (!nameStartWithIs(method)) {
            return "";
        }
        String methodName = method.getName();
        char first = Character.toLowerCase(methodName.charAt(2));
        if (methodName.length() > 3) {
            return first + methodName.substring(3);
        }
        return String.valueOf(first);
    }

    /**
     * 判断是否为 void 方法
     *
     * @param method 方法
     */
    public static boolean isVoidMethod(PsiMethod method) {
        return PsiTypeUtils.isVoid(method.getReturnType());
    }

    /**
     * 判断是否为 getXXX 函数<br/>
     * 1.方法名称getXXX<br/>
     * 2.有返回值<br/>
     *
     * @param method 方法
     */
    public static boolean isGetMethod(PsiMethod method) {
        return isPublicMethod(method)
                && !isVoidMethod(method)
                && START_GET.matcher(method.getName()).matches();
    }

    /**
     * 判断是否为 setXxx 函数<br/>
     * 1.方法名称setXxx<br/>
     * 2.有方法参数<br/>
     *
     * @param method 方法
     */
    public static boolean isSetMethod(PsiMethod method) {
        return isPublicMethod(method)
                && method.hasParameters()
                && START_SET.matcher(method.getName()).matches();
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
     * 转为 getXxx方法名
     */

    public static String toGetPrefix(final String text) {
        return "get" + Character.toUpperCase(text.charAt(0)) + (text.length() > 1 ? text.substring(1) : "");
    }
}
