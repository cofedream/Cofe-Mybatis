package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import org.jetbrains.annotations.Nullable;

/**
 * @author : zhengrf
 * @date : 2019-01-20
 */
public interface Foreach extends DomElement {

    @Nullable
    @NameValue
    @Attribute("collection")
    GenericAttributeValue<String> getCollection();

    @Nullable
    @NameValue
    @Attribute("item")
    GenericAttributeValue<String> getItem();

    @Nullable
    @NameValue
    @Attribute("index")
    GenericAttributeValue<String> getIndex();

}
