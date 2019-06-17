package tk.cofe.plugin.mybatis.dom.mapper.converter;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.Include;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.Mapper;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.Sql;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code <include>} 标签
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

    @NotNull
    @Override
    protected List<Sql> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper) {
        return mapper.getSqls();
    }

    @Override
    protected boolean filterDomElement(@NotNull Sql targetDomElement, @NotNull String selfValue) {
        return targetDomElement.getIdValue().map(id -> id.equals(selfValue)).orElse(false);
    }

    @Nullable
    @Override
    protected XmlAttributeValue getTargetElement(@NotNull Sql targetDomElement) {
        return targetDomElement.getId().getXmlAttributeValue();
    }
}
