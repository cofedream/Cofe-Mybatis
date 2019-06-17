package tk.cofe.plugin.mybatis.dom.mapper.model.attirubte;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-15
 */
public interface TestAttribute extends DomElement {

    @Required
    @NameValue
    @Nullable
    @Attribute("test")
    GenericAttributeValue<String> getTest();

    /**
     * 获取 test 值
     * @return test 值 如果为Null 则返回 ""
     */
    @NotNull
    default Optional<String> getIdValue() {
        return AttributeUtils.getAttributeVlaue(getTest());
    }
}
