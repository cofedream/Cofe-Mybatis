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

package tk.cofe.plugin.mybatis.dom.description.model.tag;

import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ColumnAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ColumnPrefixAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ForeignColumnAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.JavaTypeAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.JdbcTypeAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.NotNullColumnAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.ResultSetAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.SelectAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.attirubte.TypeHandlerAttribute;
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.Collection;

import java.util.List;

/**
 * {@code <association></association>}
 *
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface Association extends PropertyAttribute, ColumnAttribute, JavaTypeAttribute, JdbcTypeAttribute,
        SelectAttribute, ResultMapAttribute, TypeHandlerAttribute, NotNullColumnAttribute, ColumnPrefixAttribute, ResultSetAttribute, ForeignColumnAttribute {

    @SubTag("constructor")
    Constructor getConstructor();

    @SubTag("discriminator")
    Discriminator getDiscriminator();

    @SubTagList("id")
    List<Id> getIds();

    @SubTagList("result")
    List<Result> getResult();

    @SubTagList("association")
    List<Association> getAssociations();

    @SubTagList("collection")
    List<Collection> getCollections();

}
