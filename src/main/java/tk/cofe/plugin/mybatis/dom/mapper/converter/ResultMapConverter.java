package tk.cofe.plugin.mybatis.dom.mapper.converter;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.mapper.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.Mapper;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.ResultMap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 解析 "ResultMap" 标签
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class ResultMapConverter {
    /**
     * extends 属性
     */
    public static class Extends extends XmlAttributeValueConverter<ResultMap> {
        @Nullable
        @Override
        public Collection<XmlAttributeValue> getVariants(ConvertContext context, Mapper mapper) {
            ResultMap domElement = (ResultMap) DomUtil.getDomElement(context.getTag());
            if (domElement == null) {
                return Collections.emptyList();
            }
            return mapper.getResultMaps().stream()
                    .filter(resultMap -> domElement.getIdValue().map(selfId -> !selfId.equals(resultMap.getIdValue().orElse(""))).orElse(false))
                    .map(resultMap -> resultMap.getId().getXmlAttributeValue()).collect(Collectors.toList());
        }

        @Override
        protected boolean isTarget(@NotNull ConvertContext selfContext) {
            return selfContext.getInvocationElement().getParent() instanceof ResultMap;
        }

        @Override
        protected List<? extends IdAttribute> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper) {
            return mapper.getResultMaps();
        }

        @Override
        protected boolean filterDomElement(@NotNull ResultMap targetDomElement, @NotNull String selfValue) {
            return targetDomElement.getIdValue().map(id -> id.equals(selfValue)).orElse(false);
        }

        @Override
        protected XmlAttributeValue getTargetElement(@NotNull ResultMap targetDomElement) {
            return targetDomElement.getId().getXmlAttributeValue();
        }
    }
}
