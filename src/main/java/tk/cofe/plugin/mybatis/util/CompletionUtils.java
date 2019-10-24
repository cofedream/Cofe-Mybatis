/*
 * Copyright (C) 2019 cofe
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mybatis.util;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码完成相关工具类
 *
 * @author : zhengrf
 * @date : 2019-08-10
 */
public class CompletionUtils {

    private static final Pattern PARAM_PATTERN = Pattern.compile("param(?<num>\\d+)");

    /**
     * 获取前缀对应的类型
     *
     * @param prefix        前缀
     * @param psiParameters 参数数组
     * @return 前缀对应的类型
     */
    @Nullable
    public static PsiType getPrefixType(@NotNull final String prefix, @NotNull final PsiParameter[] psiParameters) {
        Matcher matcher = PARAM_PATTERN.matcher(prefix);
        if (matcher.matches()) {
            int num = Integer.parseInt(matcher.group("num")) - 1;
            if (num > psiParameters.length) {
                return null;
            }
            return psiParameters[num].getType();
        }
        if (psiParameters.length == 1) {
            PsiParameter firstParameter = psiParameters[0];
            Annotation.Value value = Annotation.PARAM.getValue(firstParameter);
            if (value == null) {
                if (PsiTypeUtils.isCustomType(firstParameter.getType())) {
                    return getTargetPsiType(prefix, firstParameter.getType());
                }
            } else if (value.getValue().equals(prefix)) {
                return firstParameter.getType();
            }
        } else {
            for (PsiParameter psiParameter : psiParameters) {
                Annotation.Value value = Annotation.PARAM.getValue(psiParameter);
                if ((value != null && prefix.equals(value.getValue()))) {
                    return psiParameter.getType();
                }
            }
        }
        return null;
    }

    /**
     * 获取前缀对应的元素
     *
     * @param prefix        前缀
     * @param psiParameters 参数数组
     * @return 前缀对应的元素
     */
    public static PsiElement getPrefixElement(@NotNull final String[] prefix, @NotNull final PsiParameter[] psiParameters) {
        PsiElement element = null;
        PsiType psiType = null;
        for (int i = 0; i < prefix.length; i++) {
            if (i == 0) {
                Matcher matcher = PARAM_PATTERN.matcher(prefix[i]);
                if (matcher.matches()) {
                    int num = Integer.parseInt(matcher.group("num")) - 1;
                    if (num > psiParameters.length) {
                        return null;
                    }
                    element = psiParameters[num];
                    psiType = psiParameters[num].getType();
                } else {
                    if (psiParameters.length == 1) {
                        PsiParameter firstParameter = psiParameters[0];
                        Annotation.Value value = Annotation.PARAM.getValue(firstParameter);
                        if (value == null) {
                            if (PsiTypeUtils.isCustomType(firstParameter.getType())) {
                                element = getTargetElement(prefix[i], firstParameter.getType());
                                psiType = getTargetPsiType(prefix[i], firstParameter.getType());
                            }
                        } else if (value.getValue().equals(prefix[i])) {
                            element = firstParameter;
                            psiType = firstParameter.getType();
                        }
                    } else {
                        for (PsiParameter psiParameter : psiParameters) {
                            Annotation.Value value = Annotation.PARAM.getValue(psiParameter);
                            if ((value != null && prefix[i].equals(value.getValue()))) {
                                element = psiParameter;
                                psiType = psiParameter.getType();
                            }
                        }
                    }
                }
            } else {
                if (PsiTypeUtils.isCustomType(psiType)) {
                    PsiClass resolve = ((PsiClassType) psiType).resolve();
                    for (PsiField field : resolve.getAllFields()) {
                        if (Objects.equals(prefix[i], field.getName())) {
                            element = field;
                            psiType = field.getType();
                        }
                    }
                    for (PsiMethod method : resolve.getAllMethods()) {
                        if (Objects.equals(processTextToGetMethodName(prefix[i]), method.getName())) {
                            element = method;
                            psiType = method.getReturnType();
                        }
                    }
                } else {
                    element = null;
                    psiType = null;
                }
            }
        }
        return element;
    }

    /**
     * 根据前缀获取目标类中字段的类型或方法的返回值类型
     *
     * @param prefix  前缀
     * @param psiType 类对象
     */
    @Nullable
    public static PsiType getTargetPsiType(@NotNull String prefix, @Nullable PsiType psiType) {
        if (!(psiType instanceof PsiClassType)) {
            return null;
        }
        final PsiClass psiClass = ((PsiClassType) psiType).resolve();
        if (psiClass == null) {
            return null;
        }
        for (PsiMember psiMember : psiClass.getAllFields()) {
            // 字段名与前缀匹配 且 为自定义类型
            if (prefix.equals(psiMember.getName()) && PsiTypeUtils.isCustomType(((PsiField) psiMember).getType())) {
                return ((PsiField) psiMember).getType();
            }
        }
        // 字段名和前缀匹配
        for (PsiMember psiMember : psiClass.getAllMethods()) {
            if (prefix.equals(PsiJavaUtils.processGetMethodName(((PsiMethod) psiMember))) && isTargetMethod(((PsiMethod) psiMember))) {
                return ((PsiMethod) psiMember).getReturnType();
            }
        }
        return null;
    }

    /**
     * 根据前缀获取目标类中字段的类型或方法的返回值类型
     *
     * @param prefix  前缀
     * @param psiType 类对象
     */
    @Nullable
    public static PsiElement getTargetElement(@NotNull String prefix, @Nullable PsiType psiType) {
        if (!(psiType instanceof PsiClassType)) {
            return null;
        }
        final PsiClass psiClass = ((PsiClassType) psiType).resolve();
        if (psiClass == null) {
            return null;
        }
        for (PsiMember psiMember : psiClass.getAllFields()) {
            // 字段名与前缀匹配 且 为自定义类型
            if (prefix.equals(psiMember.getName())) {
                return psiMember;
            }
        }
        // 字段名和前缀匹配
        for (PsiMember psiMember : psiClass.getAllMethods()) {
            if (prefix.equals(PsiJavaUtils.processGetMethodName(((PsiMethod) psiMember))) && isTargetMethod(((PsiMethod) psiMember))) {
                return psiMember;
            }
        }
        return null;
    }

    /**
     * 根据前缀获取目标类中字段的类型或方法的返回值类型
     *
     * @param prefixs 前缀
     * @param psiType 类对象
     */
    @Nullable
    public static PsiClassType getTargetPsiClass(final @NotNull String[] prefixs, @Nullable final PsiType psiType) {
        if (!(psiType instanceof PsiClassType)) {
            return null;
        }
        PsiClassType target = ((PsiClassType) psiType);
        for (int i = 1; i < prefixs.length; i++) {
            if (target == null) {
                return null;
            }
            target = getTargetPsiClass(prefixs[i], target);
        }
        return target;
    }

    /**
     * 根据前缀获取目标类中字段的类型或方法的返回值类型
     *
     * @param prefix  前缀
     * @param psiType 类对象
     */
    @Nullable
    public static PsiClassType getTargetPsiClass(@NotNull String prefix, @NotNull PsiClassType psiType) {
        PsiClass psiClass = psiType.resolve();
        if (psiClass == null) {
            return null;
        }
        for (PsiMember psiMember : psiClass.getAllFields()) {
            // 字段名与前缀匹配 且 为自定义类型
            if (prefix.equals(psiMember.getName()) && PsiTypeUtils.isCustomType(((PsiField) psiMember).getType())) {
                return (PsiClassType) ((PsiField) psiMember).getType();
            }
        }
        // 字段名和前缀匹配
        for (PsiMember psiMember : psiClass.getAllMethods()) {
            if (prefix.equals(PsiJavaUtils.processGetMethodName(((PsiMethod) psiMember)))) {
                PsiType returnType = ((PsiMethod) psiMember).getReturnType();
                // 返回值不为 null 且 为自定义类型
                if (PsiTypeUtils.isCustomType(returnType)) {
                    return (PsiClassType) returnType;
                }
            }
        }
        return null;
    }

    /**
     * 判断是否为目标字段
     *
     * @param psiField 字段
     */
    public static boolean isTargetField(@NotNull PsiField psiField) {
        return !"serialVersionUID".equals(psiField.getName());
    }

    /**
     * 判断是否为 getXXX 函数
     *
     * @param method 方法名
     */
    public static boolean isTargetMethod(@NotNull PsiMethod method) {
        return PsiJavaUtils.isPublicMethod(method) && !PsiJavaUtils.isVoidMethod(method) && !PsiJavaUtils.isNativeMethod(method) && PsiJavaUtils.isGetMethod(method);
    }

    /**
     * 获取前缀文本
     *
     * @param text 文本内容
     */
    public static String getPrefixStr(final String text) {
        if (StringUtil.isNotEmpty(text)) {
            String[] prefixArr = text.split("IntellijIdeaRulezzz ");
            if (prefixArr.length > 0) {
                return prefixArr[0];
            }
        }
        return "";
    }

    /**
     * 获取前缀数组
     *
     * @param prefix 前缀
     * @return 前缀数组
     */
    public static String[] getPrefixArr(@NotNull String prefix) {
        if (StringUtil.isEmpty(prefix) || !prefix.contains(".")) {
            return new String[0];
        }
        return prefix.substring(0, prefix.lastIndexOf(".")).split("\\.");
    }

    public static String processTextToGetMethodName(@NotNull final String text) {
        return "get" + Character.toUpperCase(text.charAt(0)) + (text.length() > 1 ? text.substring(1) : "");
    }
}
