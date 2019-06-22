package tk.cofe.plugin.mybatis.dom.description.model.attirubte;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.util.GenericValueUtils;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-15
 */
public interface ResultAttribute extends DomElement {

    @Required
    @NameValue
    @Nullable
    @Attribute("result")
    GenericAttributeValue<String> getResult();

    /**
     * 获取result值
     * @return result 值 如果为Null 则返回 ""
     */
    @NotNull
    default Optional<String> getResultValue() {
        return GenericValueUtils.getAttributeVlaue(getResult());
    }
}
