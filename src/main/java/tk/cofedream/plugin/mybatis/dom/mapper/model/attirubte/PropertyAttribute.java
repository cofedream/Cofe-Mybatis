package tk.cofedream.plugin.mybatis.dom.mapper.model.attirubte;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.dom.mapper.converter.attribute.PropertyConverter;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface PropertyAttribute extends DomElement {

    @NotNull
    @Attribute("property")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<XmlAttributeValue> getProperty();
}
