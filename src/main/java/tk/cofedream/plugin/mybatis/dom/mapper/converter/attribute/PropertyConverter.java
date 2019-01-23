package tk.cofedream.plugin.mybatis.dom.mapper.converter.attribute;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ResultMap;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;
import tk.cofedream.plugin.mybatis.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class PropertyConverter extends ResolvingConverter<XmlAttributeValue> {

    @NotNull
    @Override
    public Collection<? extends XmlAttributeValue> getVariants(ConvertContext context) {
        ResultMap resultMap = DomUtil.getParentOfType(context.getInvocationElement(), ResultMap.class, true);
        if (resultMap == null) {
            return Collections.emptyList();
        }
        return resultMap.getTypeValue()
                .map(type -> JavaPsiService.getInstance(context.getProject()).getPsiClass(type)
                        .map(psiClass -> {
                            Collection<XmlAttributeValue> result = new ArrayList<>();
                            XmlAttributeValueImpl attributeValue;
                            for (PsiField field : psiClass.getAllFields()) {
                                attributeValue = new XmlAttributeValueImpl();
                                attributeValue.setPsi(field);
                                result.add(attributeValue);
                            }
                            return result;
                        }).orElse(Collections.emptyList())).orElse(Collections.emptyList());
    }

    @Nullable
    @Override
    public XmlAttributeValue fromString(@Nullable String s, ConvertContext context) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        DomElement invocationElement = context.getInvocationElement();
        ResultMap resultMap = DomUtil.getParentOfType(invocationElement, ResultMap.class, true);
        if (resultMap == null) {
            return null;
        }
        return resultMap.getTypeValue().map(type -> JavaPsiService.getInstance(context.getProject()).getPsiClass(type).map(psiClass -> {
            PsiField field = psiClass.findFieldByName(s, true);
            if (field == null) {
                return null;
            }
            return invocationElement instanceof GenericAttributeValue ? ((GenericAttributeValue) invocationElement).getXmlAttributeValue() : null;
        }).orElse(null)).orElse(null);
    }

    @Nullable
    @Override
    public String toString(@Nullable XmlAttributeValue property, ConvertContext context) {
        if (property == null) {
            return null;
        }
        return property.getValue();
    }

    @Nullable
    @Override
    public LookupElement createLookupElement(XmlAttributeValue property) {
        if (property == null) {
            return null;
        }
        PsiField field = (PsiField) ((XmlAttributeValueImpl) property).getPsi();
        if (field == null || StringUtils.isBlank(field.getName())) {
            return null;
        }
        return LookupElementBuilder.create(field.getName());
    }

    @Nullable
    @Override
    public PsiElement resolve(XmlAttributeValue o, ConvertContext context) {
        if (o == null || StringUtils.isBlank(o.getValue())) {
            return null;
        }
        ResultMap resultMap = DomUtil.getParentOfType(context.getInvocationElement(), ResultMap.class, true);
        if (resultMap == null) {
            return null;
        }
        //return super.resolve(o, context);
        return resultMap.getTypeValue()
                .map(type -> JavaPsiService.getInstance(context.getProject()).getPsiClass(type)
                        .map(psiClass -> {
                            for (PsiField field : psiClass.getAllFields()) {
                                if (o.getValue().equals(field.getName())) {
                                    return field;
                                }
                            }
                            return null;
                        }).orElse(null)).orElse(null);
        //return null;
    }
}
