package tk.cofedream.plugin.mybatis.dom.mapper.converter;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.attirubte.IdAttribute;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Include;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Mapper;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Sql;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class IncludeConverter extends XmlAttributeValueConverter<Sql> {

    @Nullable
    @Override
    public Collection<XmlAttributeValue> getVariants(ConvertContext context, Mapper mapper) {
        return mapper.getSqls().stream().map(sql -> sql.getId().getXmlAttributeValue()).collect(Collectors.toList());
    }

    @Override
    protected boolean isTarget(@NotNull ConvertContext selfContext) {
        return selfContext.getInvocationElement().getParent() instanceof Include;
    }


    @Override
    protected List<? extends IdAttribute> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper) {
        return mapper.getSqls();
    }

    @Override
    protected boolean filterDomElement(@NotNull Sql targetDomElement, @NotNull String selfValue) {
        return targetDomElement.getIdValue().map(id -> id.equals(selfValue)).orElse(false);
    }

    @Override
    protected XmlAttributeValue getTargetElement(@NotNull Sql targetDomElement) {
        return targetDomElement.getId().getXmlAttributeValue();
    }
}
