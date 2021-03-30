/*
 * Copyright (C) 2019-2021 cofe
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
import com.intellij.util.xml.*;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.config.MybatisConstants;
import tk.cofe.plugin.mybatis.dom.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.dom.model.mix.CRUDMix;
import tk.cofe.plugin.mybatis.dom.model.tag.*;
import tk.cofe.plugin.mybatis.dom.model.tag.dynamic.Sql;

import java.util.*;

/**
 * @author : zhengrf
 * @date : 2019-01-01
 */
@Namespace(MybatisConstants.MAPPER_NAMESPACE_KEY)
public interface Mapper extends DomElement {

    String TAG_NAME = "mapper";

    List<Class<? extends CRUDMix>> BASIC_OPERATION = Collections.unmodifiableList(Arrays.asList(Select.class, Update.class, Insert.class, Delete.class));

    @Required
    @Attribute("namespace")
    GenericAttributeValue<PsiClass> getNamespace();

    /**
     * 获取 namespace属性值
     *
     * @return namespace属性值
     */
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

    @SubTagList("select")
    Select addSelect();

    @SubTagList("update")
    Update addUpdate();

    @SubTagList("insert")
    Insert addInsert();

    @SubTagList("delete")
    Delete addDelete();


    // https://www.jetbrains.org/intellij/sdk/docs/reference_guide/frameworks_and_external_apis/xml_dom_api.html#children-collections
    @SubTagsList({"insert", "update", "delete", "select"})
    List<CRUDMix> getCRUDMixs();
}
