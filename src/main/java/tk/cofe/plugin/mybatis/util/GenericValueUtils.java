package tk.cofe.plugin.mybatis.util;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 属性值相关工具
 * @author : zhengrf
 * @date : 2019-01-15
 */
public final class GenericValueUtils {

    @NotNull
    public static Optional<String> getAttributeVlaue(@Nullable GenericAttributeValue<String> attributeValue) {
        if (attributeValue == null) {
            return Optional.empty();
        }
        return StringUtils.isBlank(attributeValue.getValue()) ? Optional.empty() : Optional.of(attributeValue.getValue().trim());
    }
}
