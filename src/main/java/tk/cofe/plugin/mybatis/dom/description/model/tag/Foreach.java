package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.convert.ForeachConverter;

/**
 * @author : zhengrf
 * @date : 2019-01-20
 */
public interface Foreach extends DynamicTag, DynamicSql {

    @Nullable
    @NameValue
    @Attribute("collection")
    @Convert(ForeachConverter.Collection.class)
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
