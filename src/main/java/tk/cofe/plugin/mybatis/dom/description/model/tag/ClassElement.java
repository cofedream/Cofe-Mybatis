package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.convert.ClassElementConverter;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.util.DomUtils;

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
    @Convert(ClassElementConverter.Id.class)
    GenericAttributeValue<String> getId();

    default Optional<String> getNamespaceValue() {
        return Optional.ofNullable(DomUtils.getParentOfType(this, Mapper.class, true)).flatMap(Mapper::getNamespaceValue);
    }

}