package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.convert.ResultMapConverter;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.TypeAttirbute;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@code <resultMap></resultMap>}
 * @author : zhengrf
 * @date : 2019-01-15
 */
public interface ResultMap extends IdAttribute, TypeAttirbute {

    @Required
    @NameValue
    @Nullable
    @Attribute("id")
    GenericAttributeValue<String> getId();

    @Nullable
    @Attribute("extends")
    @Convert(ResultMapConverter.Extends.class)
    GenericAttributeValue<XmlAttributeValue> getExtends();

    // tags

    @SubTagList("id")
    List<Id> getIds();

    @SubTagList("result")
    List<Result> getResults();

    @SubTagList("association")
    List<Association> getAssociations();

    @SubTagList("collection")
    List<Collection> getCollections();

    /**
     * 获取Id值
     * @return Id 值 如果为Null 则返回 ""
     */
    @NotNull
    default Optional<String> getIdValue() {
        return DomUtils.getAttributeVlaue(getId());
    }

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
