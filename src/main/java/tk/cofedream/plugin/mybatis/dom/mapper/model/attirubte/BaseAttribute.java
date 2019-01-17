package tk.cofedream.plugin.mybatis.dom.mapper.model.attirubte;

import com.intellij.util.xml.GenericAttributeValue;
import tk.cofedream.plugin.mybatis.utils.StringUtils;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-15
 */
public interface BaseAttribute {
    static Optional<String> getAttributeVlaue(GenericAttributeValue<String> attributeValue) {
        if (attributeValue == null) {
            return Optional.empty();
        }
        return StringUtils.isBlank(attributeValue.getValue()) ? Optional.empty() : Optional.ofNullable(attributeValue.getValue());
    }
}
