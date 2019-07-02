package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.convert.SelectTagConverter;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ResultMapAttribute;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
public interface Select extends ResultMapAttribute,ClassElement {

    @Nullable
    @Attribute("resultType")
    @Convert(SelectTagConverter.ResultType.class)
    GenericAttributeValue<String> getResultType();
}
