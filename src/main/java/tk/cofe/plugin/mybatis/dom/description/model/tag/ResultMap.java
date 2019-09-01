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
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.Collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@code <resultMap></resultMap>}
 *
 * @author : zhengrf
 * @date : 2019-01-15
 */
public interface ResultMap extends IdAttribute, TypeAttirbute {

    @Required
    @Nullable
    @Attribute("id")
    GenericAttributeValue<String> getId();

    @Nullable
    @Attribute("extends")
    @Convert(ResultMapConverter.Extends.class)
    GenericAttributeValue<ResultMap> getExtends();

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
     * 获取 Extends 值
     *
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
