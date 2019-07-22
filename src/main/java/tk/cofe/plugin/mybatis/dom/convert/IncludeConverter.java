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

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Sql;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@code <include>} 标签
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class IncludeConverter extends XmlAttributeValueConverter<Sql> {

    @Nullable
    @Override
    public Collection<XmlAttributeValue> getVariants(ConvertContext context, Mapper mapper) {
        return mapper.getSqls().stream().filter(sql -> sql.getId() != null).map(sql -> sql.getId().getXmlAttributeValue()).collect(Collectors.toList());
    }

    @NotNull
    @Override
    protected List<Sql> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper) {
        return mapper.getSqls();
    }

    @Override
    protected boolean filterDomElement(@NotNull Sql targetDomElement, @NotNull String selfValue) {
        return targetDomElement.getIdValue().map(id -> id.equals(selfValue)).orElse(false);
    }

    @Nullable
    @Override
    protected XmlAttributeValue getTargetElement(@NotNull Sql targetDomElement) {
        return Optional.ofNullable(targetDomElement.getId()).map(GenericAttributeValue::getXmlAttributeValue).orElse(null);
    }
}
