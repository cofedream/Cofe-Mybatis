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

package tk.cofe.plugin.mybatis.dom.model.tag;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;
import tk.cofe.plugin.mybatis.dom.convert.ResultMapConverter;
import tk.cofe.plugin.mybatis.dom.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.dom.model.mix.IdOrResultMix;
import tk.cofe.plugin.mybatis.dom.model.tag.dynamic.Collection;

import java.util.List;
import java.util.Optional;

/**
 * ResultMap 标签
 *
 * @author : zhengrf
 * @date : 2019-01-15
 */
public interface ResultMap extends IdAttribute,
        IdOrResultMix {

    @Required
    @Attribute("id")
    @Referencing(value = ResultMapConverter.IdReferencing.class)
    GenericAttributeValue<String> getId();

    @Attribute("type")
    GenericAttributeValue<PsiClass> getType();

    @Attribute("extends")
    @Convert(ResultMapConverter.Extends.class)
    GenericAttributeValue<ResultMap> getExtends();

    @SubTag("constructor")
    Constructor getConstructor();

    @SubTag("discriminator")
    Discriminator getDiscriminator();

    @SubTagList("association")
    List<Association> getAssociations();

    @SubTagList("collection")
    List<Collection> getCollections();

    /**
     * 获取 type 值
     *
     * @return type 值 如果为Null 则返回 ""
     */
    default Optional<PsiClass> getTypeValue() {
        return Optional.ofNullable(getType().getValue());
    }

}
