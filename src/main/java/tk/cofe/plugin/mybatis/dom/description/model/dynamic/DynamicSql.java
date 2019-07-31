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

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.Bind;
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.Choose;
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.Foreach;
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.If;
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.Include;
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.Set;
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.Trim;
import tk.cofe.plugin.mybatis.dom.description.model.dynamic.Where;

import java.util.List;

/**
 * 标签
 * @author : zhengrf
 * @date : 2019-01-20
 */
public interface DynamicSql extends DomElement {

    @SubTagList("include")
    List<Include> getIncludes();

    @SubTagList("trim")
    List<Trim> getTrims();

    @SubTagList("where")
    List<Where> getWheres();

    @SubTagList("set")
    List<Set> getSets();

    @SubTagList("foreach")
    List<Foreach> getForeachs();

    @SubTagList("choose")
    List<Choose> getChooses();

    @SubTagList("if")
    List<If> getIfs();

    @SubTagList("bind")
    List<Bind> getBinds();
}
