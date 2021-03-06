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

package tk.cofe.plugin.mybatis.dom.model.mix;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import tk.cofe.plugin.mybatis.dom.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.dom.model.tag.Id;
import tk.cofe.plugin.mybatis.dom.model.tag.Result;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 标记包含 {@code <id/>} 或 {@code <result/>} 的标签
 * @author : zhengrf
 * @date : 2021-03-25
 */
public interface IdOrResultMix extends DomElement {

    @SubTagList("id")
    List<Id> getIds();

    @SubTagList("result")
    List<Result> getResults();

    default List<PropertyAttribute> getPropertyAttributes() {
        return Stream.concat(getIds().stream(), getResults().stream()).collect(Collectors.toList());
    }
}
