package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * resultMap 属性转换
 * @author : zhengrf
 * @date : 2019-07-02
 */
public class ResultMapAttributeConverter extends XmlAttributeValueConverter<tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap> {
    @Nullable
    @Override
    protected Collection<XmlAttributeValue> getVariants(ConvertContext context, Mapper mapper) {
        return mapper.getResultMaps().stream().filter(resultMap -> resultMap.getId() != null).map(resultMap -> resultMap.getId().getXmlAttributeValue()).collect(Collectors.toList());
    }

    @NotNull
    @Override
    protected List<tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper) {
        return mapper.getResultMaps();
    }

    @Override
    protected boolean filterDomElement(@NotNull tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap targetDomElement, @NotNull String selfValue) {
        return targetDomElement.getIdValue().map(id -> id.equals(selfValue)).orElse(false);
    }

    @Nullable
    @Override
    protected XmlAttributeValue getTargetElement(@NotNull tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap targetDomElement) {
        return Optional.ofNullable(targetDomElement.getId()).map(GenericAttributeValue::getXmlAttributeValue).orElse(null);
    }

}
