package tk.cofedream.plugin.mybatis.dom.mapper.model.attirubte;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.converter.attribute.JdbcTypeConverter;

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
