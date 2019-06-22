package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.convert.ResultMapConverter;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.TypeAttirbute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-15
 */
public interface ResultMap extends IdAttribute, TypeAttirbute {

    @Nullable
    @Attribute("extends")
    @Convert(ResultMapConverter.Extends.class)
    GenericAttributeValue<XmlAttributeValue> getExtends();

    /**
     * 获取 Extends 值
     * @return Extends 值 如果为Null 则返回 ""
     */
    @NotNull
    default Optional<String> getExtendsValue() {
        if (getExtends() != null) {
            XmlAttributeValue xmlAttributeValue = getExtends().getXmlAttributeValue();
            if (xmlAttributeValue != null) {
                return Optional.of(xmlAttributeValue.getValue());
            }
        }
        return Optional.empty();
    }

    @SubTagList("id")
    List<Id> getIds();

    @SubTagList("result")
    List<Result> getResults();

    @NotNull
    default List<PropertyAttribute> getPropertyAttributes() {
        return new ArrayList<PropertyAttribute>() {
            private static final long serialVersionUID = 3671821261060933651L;

            {
                this.addAll(getIds());
                this.addAll(getResults());
            }
        };
    }
}
