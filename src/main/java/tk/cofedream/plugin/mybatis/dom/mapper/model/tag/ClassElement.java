package tk.cofedream.plugin.mybatis.dom.mapper.model.tag;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.attirubte.BaseAttribute;

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
        //@Convert(MapperMethodConverter.class)
        //@Referencing(PsiMethodConverter.class)
    GenericAttributeValue<String> getId();

    /**
     * 获取Id值
     * @return Id 值 如果为Null 则返回 ""
     */
    @NotNull
    default Optional<String> getIdValue() {
        return BaseAttribute.getAttributeVlaue(getId());
    }

    default Optional<String> getNamespaceValue() {
        return Optional.ofNullable(DomUtil.getParentOfType(this, Mapper.class, true)).flatMap(Mapper::getNamespaceValue);
    }
}
