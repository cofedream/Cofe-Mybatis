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

import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.annotation.Annotation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码完成相关工具类
 *
 * @author : zhengrf
 * @date : 2019-08-10
 */
public class CompletionUtils {

    /**
     * 空字符串数组
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final Pattern PARAM_PATTERN = Pattern.compile("param(?<num>\\d+)");

    /**
     * 获取前缀对应的类型
     *
     * @param prefix        前缀
     * @param psiParameters 参数数组
     * @return 前缀对应的类型
     */
    public static PsiType getPrefixType(final String prefix, final PsiParameter[] psiParameters) {
        return getPrefixType(prefix, psiParameters,
                PsiParameter::getType,
                psiParameter -> getPrefixPsiType(prefix, psiParameter.getType())
        );
    }

    /**
     * 获取前缀对应的元素
     *
     * @param prefix        前缀
     * @param psiParameters 参数数组
     * @return 前缀对应的元素
     */
    public static PsiElement getPrefixElement(final String[] prefix, final PsiParameter[] psiParameters) {
        PrefixClass prefixClass = new PrefixClass();
        for (int i = 0; i < prefix.length; i++) {
            if (i == 0) {
                getPrefixType(prefix[0], psiParameters,
                        psiParameter -> prefixClass.setElement(psiParameter).setPsiType(psiParameter.getType()),
                        psiParameter -> prefixClass.setElement(getTargetElement(prefix[0], psiParameter.getType(), psiField -> psiField, psiMethod -> psiMethod)).setPsiType(getPrefixPsiType(prefix[0], psiParameter.getType())));
            } else {
                final String prefixStr = prefix[i];
                PsiTypeUtils.isCustomType(prefixClass.psiType, psiClassType -> customTypeProcessor(prefixClass, prefixStr, psiClassType), psiType -> prefixClass.clear());
            }
        }
        return prefixClass.element;
    }

    private static void customTypeProcessor(final PrefixClass prefixClass, final String prefixStr, final PsiClassType psiClassType) {
        PsiJavaUtils.psiClassProcessor(psiClassType,
                field -> Objects.equals(prefixStr, field.getName()), field -> prefixClass.setElement(field).setPsiType(field.getType()),
                method -> Objects.equals(PsiMethodUtils.toGetPrefix(prefixStr), method.getName()), method -> prefixClass.setElement(method).setPsiType(method.getReturnType()));
    }

    /**
     * 根据前缀获取目标类中字段的类型或方法的返回值类型
     *
     * @param prefixs 前缀
     * @param psiType 类对象
     */

    public static PsiClassType getPrefixPsiClass(final String[] prefixs, final PsiType psiType) {
        PsiType target = psiType;
        for (int i = 1; i < prefixs.length; i++) {
            if (target == null) {
                return null;
            }
            target = getTargetElement(prefixs[i], target,
                    psiField -> PsiTypeUtils.isCustomType(psiField.getType()), psiMethod -> PsiTypeUtils.isCustomType(psiMethod.getReturnType()),
                    PsiField::getType, PsiMethod::getReturnType);
        }
        return target instanceof PsiClassType ? ((PsiClassType) target) : null;
    }

    /**
     * 获取前缀文本
     *
     * @param text 文本内容
     */
    public static String getPrefixStr(final String text) {
        if (StringUtil.isNotEmpty(text)) {
            String[] prefixArr = text.trim().split(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED);
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
    public static String[] getPrefixArr(String prefix) {
        if (StringUtil.isEmpty(prefix) || !prefix.contains(".")) {
            return EMPTY_STRING_ARRAY;
        }
        return prefix.substring(0, prefix.lastIndexOf(".")).split("\\.");
    }

    /**
     * 根据前缀获取目标类中字段的类型或方法的返回值类型
     *
     * @param prefix  前缀
     * @param psiType 类对象
     */

    public static PsiType getPrefixPsiType(String prefix, PsiType psiType) {
        return getTargetElement(prefix, psiType, PsiField::getType, PsiMethod::getReturnType);
    }


    public static <T> T getTargetElement(String prefix, PsiType psiType,
                                         Function<PsiField, T> fieldProcessor, Function<PsiMethod, T> methodProcessor) {
        return getTargetElement(prefix, psiType, psiField -> true, psiMethod -> true, fieldProcessor, methodProcessor);
    }


    public static <T> T getTargetElement(String prefix, PsiType psiType,
                                         Predicate<PsiField> fieldCondition, Predicate<PsiMethod> methodCondition,
                                         Function<PsiField, T> fieldProcessor, Function<PsiMethod, T> methodProcessor) {
        if (!(psiType instanceof PsiClassType)) {
            return null;
        }
        final PsiClass psiClass = ((PsiClassType) psiType).resolve();
        if (psiClass == null) {
            return null;
        }
        for (PsiField field : psiClass.getAllFields()) {
            // 字段名与前缀匹配 且 为自定义类型
            if (prefix.equals(field.getName()) && fieldCondition.test(field)) {
                return fieldProcessor.apply(field);
            }
        }
        // 字段名和前缀匹配
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (prefix.equals(PsiMethodUtils.replaceGetPrefix(method)) && PsiMethodUtils.isGetMethod(method) && methodCondition.test(method)) {
                return methodProcessor.apply(method);
            }
        }
        return null;
    }


    public static <T> T getPrefixType(final String prefix, final PsiParameter[] psiParameters,
                                      final Function<PsiParameter, T> psiParameterProcessor,
                                      final Function<PsiParameter, T> customParameterProcessor) {
        Matcher matcher = PARAM_PATTERN.matcher(prefix);
        if (matcher.matches()) {
            int num = Integer.parseInt(matcher.group("num")) - 1;
            if (num > psiParameters.length) {
                return null;
            }
            return psiParameterProcessor.apply(psiParameters[num]);
        }
        if (psiParameters.length == 1) {
            PsiParameter firstParameter = psiParameters[0];
            Annotation.Value value = Annotation.PARAM.getValue(firstParameter);
            if (value == null) {
                if (PsiTypeUtils.isCustomType(firstParameter.getType())) {
                    return customParameterProcessor.apply(firstParameter);
                }
            } else if (value.getValue().equals(prefix)) {
                return psiParameterProcessor.apply(firstParameter);
            }
        } else {
            for (PsiParameter psiParameter : psiParameters) {
                Annotation.Value value = Annotation.PARAM.getValue(psiParameter);
                if (value != null && prefix.equals(value.getValue())) {
                    return psiParameterProcessor.apply(psiParameter);
                }
            }
        }
        return null;
    }

    private static final class PrefixClass {
        private PsiElement element;
        private PsiType psiType;

        PrefixClass() {
        }

        PrefixClass setElement(final PsiElement element) {
            this.element = element;
            return this;
        }

        PrefixClass setPsiType(final PsiType psiType) {
            this.psiType = psiType;
            return this;
        }

        void clear() {
            this.element = null;
            this.psiType = null;
        }
    }

    public static Map<String, PsiMember> getTheGetMethodAndField(@Nullable PsiClassType psiClassType) {
        if (psiClassType == null) {
            return Collections.emptyMap();
        }
        return getTheGetMethodAndField(psiClassType.resolve());
    }

    public static Map<String, PsiMember> getTheGetMethodAndField(@Nullable final PsiClass psiClass) {
        if (psiClass == null) {
            return Collections.emptyMap();
        }
        if (PsiJavaUtils.isObjectClass(psiClass)) {
            return Collections.emptyMap();
        }
        Map<String, PsiMember> res = new HashMap<>();
        for (PsiMethod method : psiClass.getMethods()) {
            if (PsiMethodUtils.isGetMethod(method)) {
                res.putIfAbsent(PsiMethodUtils.replaceGetPrefix(method), method);
            }
        }
        for (PsiField field : psiClass.getFields()) {
            if (PsiFieldUtils.notSerialField(field)) {
                res.putIfAbsent(field.getName(), field);
            }
        }
        res.putAll(getTheGetMethodAndField(psiClass.getSuperClass()));
        return res;
    }

    /**
     * 获取类字段提示
     *
     * @param psiType Java类类型
     */
    public static void getPsiClassTypeVariants(@Nullable PsiClassType psiType, final Consumer<PsiField> fieldConsumer, final Consumer<PsiMethod> methodConsumer) {
        if (psiType == null) {
            return;
        }
        getPsiClassTypeVariants(psiType.resolve(), fieldConsumer, methodConsumer);
    }

    /**
     * 获取类字段提示
     *
     * @param psiClass Java类
     */
    public static void getPsiClassTypeVariants(@Nullable PsiClass psiClass, final Consumer<PsiField> fieldConsumer, final Consumer<PsiMethod> methodConsumer) {
        if (psiClass == null) {
            return;
        }
        if (psiClass.isEnum()) {
            getMethodsVariants(psiClass.getMethods(), methodConsumer);
        } else {
            getFieldsVariants(psiClass.getAllFields(), fieldConsumer);
            getMethodsVariants(psiClass.getAllMethods(), methodConsumer);
        }
    }

    /**
     * 获取字段提示
     */
    public static void getFieldsVariants(final PsiField[] fields, final Consumer<PsiField> fieldConsumer) {
        for (PsiField field : fields) {
            if (PsiFieldUtils.notSerialField(field)) {
                fieldConsumer.accept(field);
            }
        }
    }

    /**
     * 获取方法提示
     */
    public static void getMethodsVariants(final PsiMethod[] methods, final Consumer<PsiMethod> methodConsumer) {
        for (PsiMethod method : methods) {
            if (PsiMethodUtils.isGetMethod(method) && !PsiJavaUtils.isObjectClass(method.getContainingClass())) {
                methodConsumer.accept(method);
            }
        }
    }
}
