package tk.cofe.plugin.mybatis.dom.description.model.attirubte;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.convert.ResultMapAttributeConverter;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface ResultMapAttribute extends DomElement {

    @NameValue
    @Nullable
    @Attribute("resultMap")
    @Convert(ResultMapAttributeConverter.class)
    GenericAttributeValue<XmlAttributeValue> getResultMap();
}
