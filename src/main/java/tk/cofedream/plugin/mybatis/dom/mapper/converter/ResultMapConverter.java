package tk.cofedream.plugin.mybatis.dom.mapper.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ResultMap;
import tk.cofedream.plugin.mybatis.dom.psi.providers.ResultMapReferenceProvider;
import tk.cofedream.plugin.mybatis.service.MapperService;
import tk.cofedream.plugin.mybatis.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class ResultMapConverter extends ResolvingConverter<XmlAttributeValue> implements CustomReferenceConverter<XmlAttributeValue> {
    @NotNull
    @Override
    public Collection<? extends XmlAttributeValue> getVariants(ConvertContext context) {
        ResultMap domElement = (ResultMap) DomUtil.getDomElement(context.getTag());
        if (domElement == null) {
            return Collections.emptyList();
        }
        DomElement invocationElement = context.getInvocationElement();
        List<XmlAttributeValue> res = new ArrayList<>();
        MapperService.getMapper(invocationElement).ifPresent(mapper -> mapper.getResultMaps().forEach(resultMap -> {
            if (resultMap.getId() == null) {
                return;
            }
            resultMap.getIdValue().ifPresent(id -> domElement.getIdValue().ifPresent(selfId -> {
                if (!id.equals(selfId)) {
                    res.add(resultMap.getId().getXmlAttributeValue());
                }
            }));
        }));
        return res;
    }

    @Nullable
    @Override
    public XmlAttributeValue fromString(@Nullable String s, ConvertContext context) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        DomElement invocationElement = context.getInvocationElement();
        if (invocationElement.getParent() instanceof ResultMap) {
            return MapperService.getMapper(context.getInvocationElement()).map(mapper -> {
                for (ResultMap resultMap : mapper.getResultMaps()) {
                    if (resultMap.getIdValue().isPresent() && resultMap.getIdValue().get().equals(s)) {
                        return resultMap.getId().getXmlAttributeValue();
                    }
                }
                return null;
            }).orElse(null);
        }
        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable XmlAttributeValue xmlAttributeValue, ConvertContext context) {
        return xmlAttributeValue != null ? xmlAttributeValue.getValue() : null;
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element, ConvertContext context) {
        List<PsiReference> res = new ArrayList<>();
        MapperService.getMapper(((XmlAttributeValue) element)).ifPresent(mapper -> {
            mapper.getResultMaps().forEach(resultMap -> {
                resultMap.getIdValue().ifPresent(id -> {
                    if (id.equals(value.getRawText())) {
                        res.add(new ResultMapReferenceProvider.Id.Reference(resultMap.getId().getXmlAttributeValue()));
                    }
                });
            });
        });
        return res.toArray(new PsiReference[0]);
    }

}
