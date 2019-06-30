package tk.cofe.plugin.mybatis.util;

import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.enums.AttributeEnums;

import java.util.Optional;

/**
 * 枚举工具类
 * @author : zhengrf
 * @date : 2019-01-17
 */
public final class EnumUtils {

    /**
     * 解析标签
     * @param targetEnums  目标属性枚举
     * @param xmlAttribute 需要解析的属性
     * @param <T>          目标属性
     * @return 解析结果
     */
    public static <T extends AttributeEnums> Optional<T> parse(@NotNull T[] targetEnums, @NotNull XmlAttribute xmlAttribute) {
        for (T attribute : targetEnums) {
            if (attribute.getValue().equals(xmlAttribute.getName())) {
                return Optional.of(attribute);
            }
        }
        return Optional.empty();
    }
}
