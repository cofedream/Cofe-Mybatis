package tk.cofe.plugin.mybatis.dom.mapper.model.attirubte;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.Optional;

/**
 * 属性值相关工具
 * @author : zhengrf
 * @date : 2019-01-15
 */
final class AttributeUtils {

    @NotNull
    public static Optional<String> getAttributeVlaue(@Nullable GenericAttributeValue<String> attributeValue) {
        if (attributeValue == null) {
            return Optional.empty();
        }
        return StringUtils.isBlank(attributeValue.getValue()) ? Optional.empty() : Optional.of(attributeValue.getValue().trim());
    }
}
