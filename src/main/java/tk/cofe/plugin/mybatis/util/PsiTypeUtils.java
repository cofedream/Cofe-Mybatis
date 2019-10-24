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

import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author : zhengrf
 * @date : 2019-01-08
 */
public final class PsiTypeUtils {

    private static final String LOCAL_DATE_CLASS_NAME = "java.time.LocalDate";
    private static final String LOCAL_TIME_CLASS_NAME = "java.time.LocalTime";
    private static final List<String> DATE_CLASS_NAMES = Arrays.asList(CommonClassNames.JAVA_UTIL_DATE, LOCAL_DATE_CLASS_NAME, LOCAL_TIME_CLASS_NAME);

    /**
     * 判断是否为 void 类型
     *
     * @param psiType 类型
     */
    public static boolean isVoid(@Nullable PsiType psiType) {
        return PsiPrimitiveType.VOID.equals(psiType);
    }

    /**
     * 判断是否为 String 类型
     *
     * @param psiType 类型
     */
    public static boolean isString(@Nullable PsiType psiType) {
        return psiType != null && psiType.equalsToText(CommonClassNames.JAVA_LANG_STRING);
    }

    /**
     * 判断是否为基础类型
     *
     * @param psiType 类型
     */
    public static boolean isPrimitiveType(@Nullable PsiType psiType) {
        if (psiType == null) {
            return false;
        }
        return PsiPrimitiveType.CHAR.equals(psiType)
                || PsiPrimitiveType.BOOLEAN.equals(psiType)
                || PsiPrimitiveType.BYTE.equals(psiType)
                || PsiPrimitiveType.SHORT.equals(psiType)
                || PsiPrimitiveType.INT.equals(psiType)
                || PsiPrimitiveType.LONG.equals(psiType)
                || PsiPrimitiveType.FLOAT.equals(psiType)
                || PsiPrimitiveType.DOUBLE.equals(psiType);
    }

    /**
     * 判断是否为包装类
     *
     * @param psiType 类型
     */
    public static boolean isBoxPrimitiveType(@Nullable PsiType psiType) {
        // 非Class引用类型
        if (!(psiType instanceof PsiClassReferenceType)) {
            return false;
        }
        return psiType.equalsToText(CommonClassNames.JAVA_LANG_CHARACTER)
                || psiType.equalsToText(CommonClassNames.JAVA_LANG_BOOLEAN)
                || psiType.equalsToText(CommonClassNames.JAVA_LANG_BYTE)
                || psiType.equalsToText(CommonClassNames.JAVA_LANG_SHORT)
                || psiType.equalsToText(CommonClassNames.JAVA_LANG_INTEGER)
                || psiType.equalsToText(CommonClassNames.JAVA_LANG_LONG)
                || psiType.equalsToText(CommonClassNames.JAVA_LANG_FLOAT)
                || psiType.equalsToText(CommonClassNames.JAVA_LANG_DOUBLE);
    }

    /**
     * 判断是否为基础类型或包装类
     *
     * @param psiType 类型
     */
    public static boolean isPrimitiveOrBoxType(@Nullable PsiType psiType) {
        return isPrimitiveType(psiType) || isBoxPrimitiveType(psiType);
    }

    /**
     * 判断是否为集合类型  Collection/Map
     *
     * @param psiType 类型
     */
    public static boolean isCollectionOrMapType(@Nullable PsiType psiType) {
        return com.siyeh.ig.psiutils.CollectionUtils.isCollectionClassOrInterface(psiType);
    }

    /**
     * 判断是否为 Collection 类型
     *
     * @param psiType 类型
     */
    public static boolean isCollectionType(@Nullable PsiType psiType) {
        final PsiClass resolved = PsiUtil.resolveClassInClassTypeOnly(psiType);
        if (resolved == null) {
            return false;
        }
        return InheritanceUtil.isInheritor(resolved, CommonClassNames.JAVA_UTIL_COLLECTION);
    }

    /**
     * 判断是否为 Map 类型
     *
     * @param psiType 类型
     */
    public static boolean isMapType(@Nullable PsiType psiType) {
        final PsiClass resolved = PsiUtil.resolveClassInClassTypeOnly(psiType);
        if (resolved == null) {
            return false;
        }
        return InheritanceUtil.isInheritor(resolved, CommonClassNames.JAVA_UTIL_MAP);
    }

    /**
     * 判断是否为 数组类型
     *
     * @param psiType 类型
     * @return true 是数组类型,false 非数组类型
     */
    public static boolean isArrayType(@Nullable PsiType psiType) {
        return psiType instanceof PsiArrayType;
    }

    /**
     * 判断是否为事件类型
     *
     * @param psiType 类型
     * @return true 是时间类型,false 非时间类型
     */
    public static boolean isDateType(@Nullable PsiType psiType) {
        if (psiType == null) {
            return false;
        }
        final PsiClass resolved = PsiUtil.resolveClassInClassTypeOnly(psiType);
        if (resolved == null) {
            return false;
        }
        if (InheritanceUtil.isInheritor(resolved, CommonClassNames.JAVA_UTIL_DATE)
                || InheritanceUtil.isInheritor(resolved, LOCAL_DATE_CLASS_NAME)
                || InheritanceUtil.isInheritor(resolved, LOCAL_TIME_CLASS_NAME)) {
            return true;
        }
        return DATE_CLASS_NAMES.contains(psiType.getCanonicalText());
    }

    /**
     * 是自定义类型
     *
     * @param psiType 类型
     */
    public static boolean isCustomType(@Nullable PsiType psiType) {
        if (isPrimitiveOrBoxType(psiType)
                || isString(psiType)
                || isCollectionOrMapType(psiType)
                || isDateType(psiType)
        ) {
            return false;
        }
        // 上述类型都不成立,且为类对象,则为自定义对象
        return psiType instanceof PsiClassType;
    }

    /**
     * 是自定义类型
     *
     * @param psiType  类型
     * @param consumer 为自定义类型的情况下回调执行
     * @return 判断结果
     */
    public static boolean isCustomType(@Nullable PsiType psiType, @NotNull Consumer<PsiClassType> consumer) {
        if (isCustomType(psiType)) {
            consumer.accept(((PsiClassType) psiType));
            return true;
        }
        return false;
    }

}
