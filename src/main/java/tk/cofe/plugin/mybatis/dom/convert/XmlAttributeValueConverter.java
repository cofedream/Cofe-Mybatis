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

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PlatformIcons;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.bundle.MyBatisBundle;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * XmlAttribute 基础转换器
 *
 * @param <T> 目标 DOm 元素
 * @author : zhengrf
 * @date : 2019-01-21
 */
public abstract class XmlAttributeValueConverter<T extends DomElement> extends ResolvingConverter<T> {
    @Override
    public String getErrorMessage(@Nullable final String s, final ConvertContext context) {
        return MyBatisBundle.message("error.cannot.found.message", getErrorMissingTagName(), s);
    }

    public abstract String getErrorMissingTagName();

    @NotNull
    @Override
    public Collection<? extends T> getVariants(ConvertContext context) {
        Mapper mapper = MybatisUtils.getMapper(context.getInvocationElement());
        if (mapper == null) {
            return Collections.emptyList();
        }
        Collection<T> variants = getVariants(context, mapper);
        return variants == null ? Collections.emptyList() : variants;
    }

    @Nullable
    protected abstract Collection<T> getVariants(ConvertContext context, Mapper mapper);

    /**
     * 根据字符串值转成目标元素
     *
     * @param value   字符值
     * @param context 字符元素
     * @return 目标元素
     */
    @Nullable
    @Override
    public T fromString(@Nullable String value, ConvertContext context) {
        if (StringUtil.isEmpty(value) || context == null) {
            return null;
        }
        Mapper mapper = MybatisUtils.getMapper(context.getInvocationElement());
        if (mapper == null) {
            return null;
        }
        return findTargetElement(value, context, mapper);
    }

    /**
     * 找到当前元素值引用的目标源元素
     *
     * @param value         值
     * @param context       当前元素
     * @param currentMapper 当前MapperXMl
     * @return 目标元素
     */
    @Nullable
    private T findTargetElement(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper currentMapper) {
        return getReferenceDomElements(value, context, currentMapper).stream()
                .filter(targetDom -> filterDomElement(targetDom, value))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取可能引用的 Dom 元素
     *
     * @param value   值
     * @param context 当前元素
     * @param mapper  当前MapperXML
     * @return Id属性列表
     */
    @NotNull
    protected abstract List<T> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper);

    /**
     * 判断是否为目标元素
     *
     * @param targetDomElement 目标元素
     * @param selfValue        当前值
     * @return {@code true} 则为目标元素
     */
    protected abstract boolean filterDomElement(@NotNull T targetDomElement, @NotNull String selfValue);

    @Nullable
    @Override
    public LookupElement createLookupElement(final T t) {
        if (t == null) {
            return null;
        }
        return Optional.ofNullable(toString(t, null)).map(text -> LookupElementBuilder.create(text).withIcon(PlatformIcons.XML_TAG_ICON)).orElse(null);
    }

}
