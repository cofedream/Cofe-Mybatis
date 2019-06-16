package tk.cofe.plugin.mybatis.dom.mapper.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.service.MapperService;
import tk.cofe.plugin.mybatis.util.CollectionUtils;
import tk.cofe.plugin.mybatis.util.StringUtils;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * XmlAttribute 基础转换器
 * @param <T> 目标 DOm 元素
 * @author : zhengrf
 * @date : 2019-01-21
 */
public abstract class XmlAttributeValueConverter<T extends DomElement> extends ResolvingConverter<XmlAttributeValue> {

    @NotNull
    @Override
    public Collection<? extends XmlAttributeValue> getVariants(ConvertContext context) {
        return MapperService.getMapper(context.getInvocationElement()).map(mapper -> {
            Collection<XmlAttributeValue> variants = getVariants(context, mapper);
            if (!CollectionUtils.isEmpty(variants)) {
                return variants;
            }
            return new ArrayList<XmlAttributeValue>();
        }).orElse(Collections.emptyList());
    }

    @Nullable
    protected abstract Collection<XmlAttributeValue> getVariants(ConvertContext context, Mapper mapper);

    /**
     * 根据字符串值转成目标元素
     * @param selfValue   字符值
     * @param selfContext 字符元素
     * @return 目标元素
     */
    @Nullable
    @Override
    public XmlAttributeValue fromString(@Nullable String selfValue, ConvertContext selfContext) {
        if (StringUtils.isBlank(selfValue) || selfContext == null) {
            return null;
        }
        if (isTarget(selfContext)) {
            return MapperService.getMapper(selfContext.getInvocationElement()).map(mapper -> findTargetElement(selfValue, selfContext, mapper)).orElse(null);
        }
        return null;
    }

    /**
     * 判断是否为目标元素
     * @param selfContext 当前元素
     */
    protected abstract boolean isTarget(@NotNull ConvertContext selfContext);

    /**
     * 找到当前元素值引用的目标源元素
     * @param selfValue     值
     * @param selfContext   当前元素
     * @param currentMapper 当前MapperXMl
     * @return 目标元素
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private XmlAttributeValue findTargetElement(@NotNull String selfValue, @NotNull ConvertContext selfContext, @NotNull Mapper currentMapper) {
        return getReferenceDomElements(selfValue, selfContext, currentMapper).stream()
                .filter(targetDom -> filterDomElement(((T) targetDom), selfValue))
                .findFirst()
                .map(target -> getTargetElement(((T) target))).orElse(null);
    }

    /**
     * 获取可能引用的 Dom 元素
     * @param value   值
     * @param context 当前元素
     * @param mapper  当前MapperXMl
     * @return Id属性列表
     */
    protected abstract List<? extends DomElement> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper);

    /**
     * 判断是否为目标元素
     * @param targetDomElement 目标元素
     * @param selfValue        当前值
     * @return {@code true} 则为目标元素
     */
    protected abstract boolean filterDomElement(@NotNull T targetDomElement, @NotNull String selfValue);

    /**
     * 获取目标节点
     * @param targetDomElement 目标元素
     * @return 目标节点
     */
    protected abstract XmlAttributeValue getTargetElement(@NotNull T targetDomElement);

    @Nullable
    @Override
    public String toString(@Nullable XmlAttributeValue xmlAttributeValue, ConvertContext context) {
        return xmlAttributeValue == null ? null : xmlAttributeValue.getValue();
    }

    @Nullable
    @Override
    public LookupElement createLookupElement(XmlAttributeValue xmlAttributeValue) {
        return LookupElementBuilder.create(xmlAttributeValue.getValue());
    }
}
