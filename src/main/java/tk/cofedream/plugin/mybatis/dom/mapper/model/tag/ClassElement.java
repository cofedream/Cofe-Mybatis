package tk.cofedream.plugin.mybatis.dom.mapper.model.tag;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.converter.attribute.ClassElementConvert;
import tk.cofedream.plugin.mybatis.dom.mapper.model.attirubte.IdAttribute;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
public interface ClassElement extends IdAttribute, DynamicSql {

    @Required
    @NameValue
    @Nullable
    @Attribute("id")
    @Convert(ClassElementConvert.Id.class)
    GenericAttributeValue<String> getId();

    default Optional<String> getNamespaceValue() {
        return Optional.ofNullable(DomUtil.getParentOfType(this, Mapper.class, true)).flatMap(Mapper::getNamespaceValue);
    }
}
