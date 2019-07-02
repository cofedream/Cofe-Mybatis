package tk.cofe.plugin.mybatis.dom.description.model.attirubte;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-06-30
 */
public interface OfTypeAttribute extends DomElement {

    @Required
    @NameValue
    @Nullable
    @Attribute("ofType")
    GenericAttributeValue<String> getOfType();


    /**
     * 获取 ofType 值
     * @return ofType 值 如果为Null 则返回 ""
     */
    @NotNull
    default Optional<String> getOfTypeValue() {
        return DomUtils.getAttributeVlaue(getOfType());
    }
}
