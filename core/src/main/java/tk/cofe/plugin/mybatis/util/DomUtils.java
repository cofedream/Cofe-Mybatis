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
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomTarget;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-01
 */
public final class DomUtils extends DomUtil {

    public static Optional<DomTarget> resolveToDomTarget(PsiElement element) {
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
    public static <T extends DomElement> List<T> getParents(@Nullable final PsiElement element, Class<? extends PsiElement> psiElementType, final Class<T> domElementType) {
        return getParents(element, psiElementType, domElementType, true);
    }

    @NotNull
    public static <T extends DomElement> List<T> getParents(@Nullable final PsiElement element, Class<? extends PsiElement> psiElementType, final Class<T> domElementType, final boolean strict) {
        if (element == null) {
            return Collections.emptyList();
        }
        PsiElement currentElement = element;
        if (strict) {
            currentElement = PsiTreeUtil.getParentOfType(element, psiElementType);
        }
        final T domElement = getDomElement(currentElement, domElementType).orElse(null);
        if (domElement == null) {
            return Collections.emptyList();
        } else {
            List<T> res = new ArrayList<>();
            res.add(domElement);
            res.addAll(getParents(currentElement, psiElementType, domElementType));
            return res;
        }
    }

    public static <T extends DomElement> Optional<T> getDomElement(@Nullable PsiElement element, final Class<T> requiredClass) {
        return getDomElement(element, requiredClass, true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends DomElement> Optional<T> getDomElement(@Nullable PsiElement element, final Class<T> requiredClass, boolean scanParent) {
        DomElement domElement = DomUtils.getDomElement(element);
        if (requiredClass.isInstance(domElement)) {
            return Optional.of((T) domElement);
        }
        if (scanParent) {
            return Optional.ofNullable(DomUtils.getParentOfType(domElement, requiredClass, true));
        }
        return Optional.empty();
    }

    /**
     * 是否为目标类型元素
     *
     * @param xmlTag        元素
     * @param requiredClass 目标类型
     */
    public static boolean isTargetDomElement(@Nullable XmlTag xmlTag, @NotNull Class<?> requiredClass) {
        final DomElement domElement = DomUtils.getDomElement(xmlTag);
        if (domElement == null) {
            return false;
        }
        return requiredClass.isInstance(domElement);
    }

    /**
     * 是否为目标类型元素或目标元素的子级元素
     *
     * @param element       元素
     * @param requiredClass 目标类型
     */
    public static boolean isTargetDomElement(PsiElement element, Class<?> requiredClass) {
        return isTargetDomElement(element, requiredClass, true);
    }

    /**
     * 是否为目标类型元素或目标元素的子级元素
     *
     * @param element       元素
     * @param requiredClass 目标类型
     * @param scanParent    是否扫描父级
     */
    public static boolean isTargetDomElement(PsiElement element, Class<?> requiredClass, boolean scanParent) {
        DomElement domElement = DomUtils.getDomElement(element);
        if (requiredClass.isInstance(domElement)) {
            return true;
        }
        return scanParent && Optional.ofNullable(DomUtils.getParentOfType(domElement, requiredClass, true)).isPresent();
    }

    /**
     * 获取 {@code GenericAttributeValue<String> } 属性值值
     *
     * @param attributeValue 属性值对象
     * @return NULL 则返回 {@code Optional.empty()}
     */
    public static Optional<String> getAttributeVlaue(@Nullable GenericAttributeValue<?> attributeValue) {
        if (attributeValue == null) {
            return Optional.empty();
        }
        final XmlAttributeValue xmlAttributeValue = attributeValue.getXmlAttributeValue();
        if (xmlAttributeValue == null) {
            return Optional.empty();
        }
        String value = xmlAttributeValue.getValue().trim();
        if (StringUtil.isEmpty(value)) {
            return Optional.empty();
        }
        return Optional.of(value.trim());
    }

    public static XmlTag getXmlTag(@Nullable final PsiElement element) {
        if (!(element instanceof XmlElement)) {
            return null;
        }
        final DomElement domElement = DomUtils.getDomElement(element);
        if (domElement == null) {
            return null;
        }
        return domElement.getXmlTag();
    }
}
