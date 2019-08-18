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

package tk.cofe.plugin.mybatis.dom.description.model.dynamic;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.convert.ForeachConverter;

/**
 * @author : zhengrf
 * @date : 2019-01-20
 */
public interface Foreach extends DynamicTag, DynamicSql {

    @Required
    @NotNull
    @Attribute("collection")
    @Convert(ForeachConverter.Collection.class)
    GenericAttributeValue<String> getCollection();

    @Nullable
    @Attribute("item")
    GenericAttributeValue<String> getItem();

    @Nullable
    @Attribute("index")
    GenericAttributeValue<String> getIndex();

}
