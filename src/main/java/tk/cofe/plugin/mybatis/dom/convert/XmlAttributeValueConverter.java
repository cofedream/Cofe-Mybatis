package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Mapper;
import tk.cofe.plugin.mybatis.service.MapperService;
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        return MapperService.getMapper(context.getInvocationElement()).map(mapper -> Optional.ofNullable(getVariants(context, mapper)).orElse(Collections.emptyList())).orElse(Collections.emptyList());
    }

    @Nullable
    protected abstract Collection<XmlAttributeValue> getVariants(ConvertContext context, Mapper mapper);

    /**
     * 根据字符串值转成目标元素
     * @param value   字符值
     * @param context 字符元素
     * @return 目标元素
     */
    @Nullable
    @Override
    public XmlAttributeValue fromString(@Nullable String value, ConvertContext context) {
        if (StringUtils.isBlank(value) || context == null) {
            return null;
        }
        return isTarget(context) ? MapperService.getMapper(context.getInvocationElement()).map(mapper -> findTargetElement(value, context, mapper)).orElse(null) : null;
    }

    /**
     * 判断是否为目标元素
     * @param selfContext 当前元素
     */
    protected abstract boolean isTarget(@NotNull ConvertContext selfContext);

    /**
     * 找到当前元素值引用的目标源元素
     * @param value         值
     * @param context       当前元素
     * @param currentMapper 当前MapperXMl
     * @return 目标元素
     */
    @Nullable
    private XmlAttributeValue findTargetElement(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper currentMapper) {
        return getReferenceDomElements(value, context, currentMapper).stream()
                .filter(targetDom -> filterDomElement(targetDom, value))
                .findFirst()
                .map(this::getTargetElement).orElse(null);
    }

    /**
     * 获取可能引用的 Dom 元素
     * @param value   值
     * @param context 当前元素
     * @param mapper  当前MapperXMl
     * @return Id属性列表
     */
    @NotNull
    protected abstract List<T> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper);

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
    @Nullable
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
