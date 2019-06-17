package tk.cofe.plugin.mybatis.dom.mapper.model.attirubte;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.mapper.converter.attribute.PropertyConverter;

/**
 * 字段属性
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface PropertyAttribute extends DomElement {

    @NotNull
    @Attribute("property")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<String> getProperty();
}
