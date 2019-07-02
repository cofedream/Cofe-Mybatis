package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.JavaTypeAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.JdbcTypeAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.SelectAttribute;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface Collection extends PropertyAttribute, JavaTypeAttribute, JdbcTypeAttribute, SelectAttribute, ResultMapAttribute {

    @SubTagList("id")
    List<Id> getIds();

    @SubTagList("result")
    List<Result> getResults();

    @SubTagList("association")
    List<Association> getAssociations();

    @SubTagList("collection")
    List<Collection> getCollections();

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
