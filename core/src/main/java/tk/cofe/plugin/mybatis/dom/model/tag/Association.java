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

package tk.cofe.plugin.mybatis.dom.model.tag;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;
import tk.cofe.plugin.mybatis.dom.model.attirubte.*;
import tk.cofe.plugin.mybatis.dom.model.mix.IdOrResultMix;

import java.util.List;

/**
 * association 标签
 *
 * @author : zhengrf
 * @date : 2019-01-21
 */
public interface Association extends PropertyAttribute, ColumnAttribute, JavaTypeAttribute, JdbcTypeAttribute,
        SelectAttribute, ResultMapAttribute, TypeHandlerAttribute, NotNullColumnAttribute, ColumnPrefixAttribute, ResultSetAttribute, ForeignColumnAttribute,
        IdOrResultMix {

    @Attribute("javaType")
    GenericAttributeValue<PsiClass> getJavaType();

    @SubTag("constructor")
    Constructor getConstructor();

    @SubTag("discriminator")
    Discriminator getDiscriminator();

    @SubTagList("association")
    List<Association> getAssociations();

    @SubTagList("collection")
    List<Collection> getCollections();

}
