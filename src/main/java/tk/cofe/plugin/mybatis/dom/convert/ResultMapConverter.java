package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Collection;
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
            ResultMap domElement = (ResultMap) DomUtils.getDomElement(context.getTag());
            if (domElement == null) {
                return null;
            }
            return mapper.getResultMaps().stream().filter(resultMap -> domElement.getIdValue().map(id -> !id.equals(resultMap.getIdValue().orElse(null))).orElse(false))
                    .map(resultMap -> resultMap.getId() == null ? null : resultMap.getId().getXmlAttributeValue())
                    .collect(Collectors.toList());
        }

        @Override
        protected boolean isTarget(@NotNull ConvertContext context) {
            return context.getInvocationElement().getParent() instanceof ResultMap;
        }

        @NotNull
        @Override
        protected List<ResultMap> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper) {
            return mapper.getResultMaps();
        }

        @Override
        protected boolean filterDomElement(@NotNull ResultMap targetDomElement, @NotNull String selfValue) {
            return targetDomElement.getIdValue().map(id -> id.equals(selfValue)).orElse(false);
        }

        @Nullable
        @Override
        protected XmlAttributeValue getTargetElement(@NotNull ResultMap targetDomElement) {
            return targetDomElement.getId() == null ? null : targetDomElement.getId().getXmlAttributeValue();
        }
    }
}
