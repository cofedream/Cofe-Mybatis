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

package tk.cofe.plugin.mybatis.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Namespace;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Sql;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.model.tag.Delete;
import tk.cofe.plugin.mybatis.dom.model.tag.Insert;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.dom.model.tag.Select;
import tk.cofe.plugin.mybatis.dom.model.tag.Update;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-01
 */
@Namespace(Mapper.NAMESPACE_KEY)
public interface Mapper extends DomElement {

    @NonNls
    String MAPPER_DTD_URL = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";
    @NonNls
    String MAPPER_DTD_ID = "-//mybatis.org//DTD Mapper 3.0//EN";
    /**
     * All mapper.xml DTD-IDs/URIs.
     */
    @NonNls
    String[] DTDS = {
            MAPPER_DTD_URL, MAPPER_DTD_ID
    };
    /**
     * DOM-Namespace-Key for mapper.xml
     */
    @NonNls
    String NAMESPACE_KEY = "mapper namespace";
    String TAG_NAME = "mapper";

    List<Class<? extends ClassElement>> BASIC_OPERATION = Arrays.asList(Select.class, Update.class, Insert.class, Delete.class);

    @NotNull
    @Required
    @Attribute("namespace")
    GenericAttributeValue<PsiClass> getNamespace();

    /**
     * 获取 namespace属性值
     *
     * @return namespace属性值
     */
    @NotNull
    default Optional<PsiClass> getNamespaceValue() {
        return Optional.ofNullable(getNamespace().getValue());
    }

    /**
     * 判断是否为目标Mapper XML文件
     *
     * @param psiClass 需判断的类
     * @return true 是目标 Mapper XML , false 非目标Mapper XML
     */
    default boolean isTargetMapper(@Nullable PsiClass psiClass) {
        return Objects.equals(getNamespace().getValue(), psiClass);
    }

    @SubTagList("resultMap")
    List<ResultMap> getResultMaps();

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

    /**
     * 获取ID标签对应的元素
     *
     * @param id ID标签
     */
    default List<? extends IdAttribute> getIdElements(IdAttribute id) {
        if (id instanceof Sql) {
            return getSqls();
        } else if (id instanceof Insert) {
            return getInserts();
        } else if (id instanceof Select) {
            return getSelects();
        } else if (id instanceof Update) {
            return getUpdates();
        } else if (id instanceof Delete) {
            return getDeletes();
        } else if (id instanceof ResultMap) {
            return getResultMaps();
        } else {
            return Collections.emptyList();
        }
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
