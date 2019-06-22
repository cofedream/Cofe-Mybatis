package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.convert.SelectTagConverter;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
public interface Select extends ClassElement {

    @Nullable
    @Attribute("resultMap")
    @Convert(SelectTagConverter.ResultMap.class)
    GenericAttributeValue<XmlAttributeValue> getResultMap();

    @Nullable
    @Attribute("resultType")
    @Convert(SelectTagConverter.ResultType.class)
    GenericAttributeValue<String> getResultType();
}
