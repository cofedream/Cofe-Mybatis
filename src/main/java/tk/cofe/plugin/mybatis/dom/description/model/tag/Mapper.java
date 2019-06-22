package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Namespace;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTagList;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.constants.Mybatis;
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-01
 */
@Namespace(Mybatis.Mapper.NAMESPACE_KEY)
public interface Mapper extends DomElement {
    List<Class<? extends ClassElement>> BASIC_OPERATION = Arrays.asList(Select.class, Update.class, Insert.class, Delete.class);

    String TAG_NAME = "mapper";

    @Required
    @NameValue
    @NotNull
    @Attribute("namespace")
    GenericAttributeValue<String> getNamespace();

    /**
     * 获取 namespace属性值
     * @return namespace属性值
     */
    @NotNull
    default Optional<String> getNamespaceValue() {
        String value = getNamespace().getValue();
        return Optional.ofNullable(StringUtils.isBlank(value) ? null : value);
    }

    @SubTagList("resultMap")
    List<ResultMap> getResultMaps();

    @NonNull
    default List<ClassElement> getClassElements() {
        List<ClassElement> classElements = new LinkedList<>();
        if (getInserts() != null) {
            classElements.addAll(getInserts());
        }
        if (getUpdates() != null) {
            classElements.addAll(getUpdates());
        }
        if (getDeletes() != null) {
            classElements.addAll(getDeletes());
        }
        if (getSelects() != null) {
            classElements.addAll(getSelects());
        }
        return classElements;
    }

    @SubTagList("insert")
    List<Insert> getInserts();

    @SubTagList("update")
    List<Update> getUpdates();

    @SubTagList("delete")
    List<Delete> getDeletes();

    @SubTagList("select")
    List<Select> getSelects();

    @SubTagList("sql")
    List<Sql> getSqls();

    @SubTagList("select")
    Select addSelect();

    @SubTagList("update")
    Update addUpdate();

    @SubTagList("insert")
    Insert addInsert();

    @SubTagList("delete")
    Delete addDelete();


    // https://www.jetbrains.org/intellij/sdk/docs/reference_guide/frameworks_and_external_apis/xml_dom_api.html#children-collections
    //@SubTagsList({"insert", "update", "delete", "select"})
    //List<ClassElement> getClassElements();
    //@SubTagsList(value = {"insert", "update", "delete", "select"},tagName = "insert")
    //ClassElement addInsertElements();
    //@SubTagsList(value = {"insert", "update", "delete", "select"},tagName = "update")
    //ClassElement addUpdateElements();
    //@SubTagsList(value = {"insert", "update", "delete", "select"},tagName = "delete")
    //ClassElement addDeleteElements();
    //@SubTagsList(value = {"insert", "update", "delete", "select"},tagName = "select")
    //ClassElement addSelectElements();
}
