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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.pom.PomTarget;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomTarget;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-01
 */
public final class DomUtils extends DomUtil {

    public static Optional<DomTarget> resolveToDomTarget(@NotNull PsiElement element) {
        if (element instanceof DomTarget) {
            return Optional.of(((DomTarget) element));
        }
        if (element instanceof PomTargetPsiElement) {
            PomTarget target = ((PomTargetPsiElement) element).getTarget();
            return target instanceof DomTarget ? Optional.of(((DomTarget) target)) : Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * strict default to true
     */
    public static <T> T getParentOfType(final DomElement domElement, final Class<T> requiredClass) {
        return DomUtils.getParentOfType(domElement, requiredClass, true);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T extends DomElement> Optional<T> getDomElement(@Nullable PsiElement element, @NotNull final Class<T> requiredClass) {
        DomElement domElement = DomUtils.getDomElement(element);
        if (requiredClass.isInstance(domElement)) {
            return Optional.of((T) domElement);
        }
        return Optional.ofNullable(DomUtils.getParentOfType(domElement, requiredClass, true));
    }

    public static boolean isTargetDomElement(@Nullable PsiElement element, @NotNull Class<?> requiredClass) {
        DomElement domElement = DomUtils.getDomElement(element);
        if (requiredClass.isInstance(domElement)) {
            return true;
        }
        return Optional.ofNullable(DomUtils.getParentOfType(domElement, requiredClass, true)).isPresent();
    }

    /**
     * 获取 {@code GenericAttributeValue<String> } 属性值值
     *
     * @param attributeValue 属性值对象
     * @return NULL 则返回 {@code Optional.empty()}
     */
    @NotNull
    public static Optional<String> getAttributeVlaue(@NotNull GenericAttributeValue<?> attributeValue) {
        String value = attributeValue.getStringValue();
        if (StringUtil.isEmpty(value)) {
            return Optional.empty();
        }
        return StringUtil.isEmpty(value.trim()) ? Optional.empty() : Optional.of(value.trim());
    }
}
