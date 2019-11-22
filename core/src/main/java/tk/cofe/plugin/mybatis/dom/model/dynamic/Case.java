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

package tk.cofe.plugin.mybatis.dom.model.dynamic;

import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ResultTypeAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ValueAttribute;
import tk.cofe.plugin.mybatis.dom.model.tag.Association;
import tk.cofe.plugin.mybatis.dom.model.tag.Constructor;
import tk.cofe.plugin.mybatis.dom.model.tag.Discriminator;
import tk.cofe.plugin.mybatis.dom.model.tag.Id;
import tk.cofe.plugin.mybatis.dom.model.tag.Result;

import java.util.List;

/**
 * {@code <case/>} 标签
 *
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface Case extends ValueAttribute, ResultMapAttribute, ResultTypeAttribute {

    @SubTag("constructor")
    Constructor getConstructor();

    @SubTag("discriminator")
    Discriminator getDiscriminator();

    @SubTag("id")
    List<Id> getIds();

    @SubTagList("result")
    List<Result> getResults();

    @SubTagList("association")
    List<Association> getAssociations();

    @SubTagList("collection")
    List<Collection> getCollections();

}
