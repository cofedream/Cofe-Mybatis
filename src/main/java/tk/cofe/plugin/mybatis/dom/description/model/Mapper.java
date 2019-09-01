/*
 * Copyright (C) 2019 cofe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mybatis.dom.description.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Namespace;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.constants.Mybatis;
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.Sql;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Delete;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Insert;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Select;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Update;

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

    @NotNull
    @Required
    @Attribute("namespace")
    GenericAttributeValue<PsiClass> getNamespace();

    /**
     * 获取 namespace属性值
     * @return namespace属性值
     */
    @NotNull
    default Optional<PsiClass> getNamespaceValue() {
        return Optional.ofNullable(getNamespace().getValue());
    }

    @SubTagList("resultMap")
    List<ResultMap> getResultMaps();

    @NotNull
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

    @NotNull
    @SubTagList("select")
    Select addSelect();

    @NotNull
    @SubTagList("update")
    Update addUpdate();

    @NotNull
    @SubTagList("insert")
    Insert addInsert();

    @NotNull
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
