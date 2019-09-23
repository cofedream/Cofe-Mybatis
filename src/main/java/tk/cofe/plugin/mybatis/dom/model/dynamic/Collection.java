/*
 * Copyright (C) 2019 cofe
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mybatis.dom.model.dynamic;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ColumnAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ColumnPrefixAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ForeignColumnAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.JavaTypeAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.JdbcTypeAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.NotNullColumnAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ResultSetAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.SelectAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.TypeHandlerAttribute;
import tk.cofe.plugin.mybatis.dom.model.tag.Association;
import tk.cofe.plugin.mybatis.dom.model.tag.Constructor;
import tk.cofe.plugin.mybatis.dom.model.tag.Discriminator;
import tk.cofe.plugin.mybatis.dom.model.tag.Id;
import tk.cofe.plugin.mybatis.dom.model.tag.Result;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface Collection extends PropertyAttribute, ColumnAttribute, JavaTypeAttribute, JdbcTypeAttribute,
        SelectAttribute, ResultMapAttribute, TypeHandlerAttribute, NotNullColumnAttribute, ColumnPrefixAttribute, ResultSetAttribute, ForeignColumnAttribute {

    @Attribute("ofType")
    GenericAttributeValue<PsiClass> getOfType();

    @SubTag("constructor")
    Constructor getConstructor();

    @SubTag("discriminator")
    Discriminator getDiscriminator();

    @SubTagList("id")
    List<Id> getIds();

    @SubTagList("result")
    List<Result> getResults();

    @SubTagList("association")
    List<Association> getAssociations();

    @SubTagList("collection")
    List<Collection> getCollections();


    /**
     * 获取 ofType 值
     *
     * @return ofType 值 如果为Null 则返回 ""
     */
    @NotNull
    default Optional<PsiClass> getOfTypeValue() {
        return Optional.ofNullable(getOfType().getValue());
    }

}
