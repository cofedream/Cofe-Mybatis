package tk.cofe.plugin.mybatis.dom.description.model.attirubte;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-06-30
 */
public interface JavaTypeAttribute extends DomElement {

    @NameValue
    @Nullable
    @Attribute("javaType")
    GenericAttributeValue<String> getJavaType();

    /**
     * 获取 javaType 值
     * @return javaType 值 如果为Null 则返回 ""
     */
    @NotNull
    default Optional<String> getJavaTypeValue() {
        return DomUtils.getAttributeVlaue(getJavaType());
    }
}
