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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * resultMap 属性转换
 * @author : zhengrf
 * @date : 2019-07-02
 */
public class ResultMapAttributeConverter extends XmlAttributeValueConverter<tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap> {
    @Nullable
    @Override
    protected Collection<XmlAttributeValue> getVariants(ConvertContext context, Mapper mapper) {
        return mapper.getResultMaps().stream().filter(resultMap -> resultMap.getId() != null).map(resultMap -> resultMap.getId().getXmlAttributeValue()).collect(Collectors.toList());
    }

    @NotNull
    @Override
    protected List<tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper) {
        return mapper.getResultMaps();
    }

    @Override
    protected boolean filterDomElement(@NotNull tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap targetDomElement, @NotNull String selfValue) {
        return targetDomElement.getIdValue().map(id -> id.equals(selfValue)).orElse(false);
    }

    @Nullable
    @Override
    protected XmlAttributeValue getTargetElement(@NotNull tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap targetDomElement) {
        return Optional.ofNullable(targetDomElement.getId()).map(GenericAttributeValue::getXmlAttributeValue).orElse(null);
    }

}
