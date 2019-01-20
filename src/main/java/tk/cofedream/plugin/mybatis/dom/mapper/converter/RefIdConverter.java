package tk.cofedream.plugin.mybatis.dom.mapper.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class RefIdConverter extends ResolvingConverter<XmlAttributeValue> implements CustomReferenceConverter<XmlAttributeValue> {
    @NotNull
    @Override
    public Collection<? extends XmlAttributeValue> getVariants(ConvertContext context) {
        // todo 方法完成调用
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public XmlAttributeValue fromString(@Nullable String s, ConvertContext context) {
        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable XmlAttributeValue xmlAttributeValue, ConvertContext context) {
        return null;
    }

    @NotNull
    @Override
    public PsiReference[] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element, ConvertContext context) {
        return new PsiReference[0];
    }
}
