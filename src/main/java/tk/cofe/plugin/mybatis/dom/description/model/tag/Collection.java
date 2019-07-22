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
