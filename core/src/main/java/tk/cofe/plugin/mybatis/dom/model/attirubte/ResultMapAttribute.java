/*
 * Copyright (C) 2019-2022 cofe
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

package tk.cofe.plugin.mybatis.dom.model.attirubte;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import tk.cofe.plugin.mybatis.dom.convert.ResultMapConverter;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;

/**
 * ResultMap 属性
 *
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface ResultMapAttribute extends DomElement {

    @Attribute("resultMap")
    @Convert(ResultMapConverter.class)
    GenericAttributeValue<ResultMap> getResultMap();

}
