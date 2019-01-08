package tk.cofedream.plugin.mybatis.dom.mapper.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.utils.StringUtils;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
public interface ClassElement extends DomElement {

    @Required
    @NameValue
    @Nullable
    @Attribute("id")
    GenericAttributeValue<String> getId();

    /**
     * 获取Id值
     * @return Id 值 如果为Null 则返回 ""
     */
    @NotNull
    default Optional<String> getIdValue() {
        GenericAttributeValue<String> id = getId();
        if (id == null) {
            return Optional.empty();
        }
        return StringUtils.isBlank(id.getValue()) ? Optional.empty() : Optional.ofNullable(id.getValue());
    }

    default Optional<String> getNamespaceValue() {
        return Optional.ofNullable(DomUtil.getParentOfType(this, MapperXml.class, true)).flatMap(MapperXml::getNamespaceValue);
    }
}
