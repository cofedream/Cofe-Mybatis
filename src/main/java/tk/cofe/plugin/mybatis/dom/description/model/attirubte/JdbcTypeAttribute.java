package tk.cofe.plugin.mybatis.dom.description.model.attirubte;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.convert.JdbcTypeConverter;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface JdbcTypeAttribute extends DomElement {

    @Nullable
    @Attribute("jdbcType")
    @Convert(JdbcTypeConverter.class)
    GenericAttributeValue<String> getJdbcType();
}
