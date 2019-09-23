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

import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 与 ResultMap 有关的标签或属性
 *
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class ResultMapConverter {

    private static abstract class Base extends XmlAttributeValueConverter<ResultMap> {
        @Override
        public String getErrorMissingTagName() {
            return "ResultMap";
        }

        @NotNull
        @Override
        protected List<ResultMap> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper) {
            return mapper.getResultMaps();
        }

        @Override
        protected boolean filterDomElement(@NotNull ResultMap targetDomElement, @NotNull String selfValue) {
            return targetDomElement.getIdValue().map(id -> id.equals(selfValue)).orElse(false);
        }

        @Nullable
        @Override
        public ResultMap fromString(@Nullable final String value, final ConvertContext context) {
            return super.fromString(value, context);
        }

        @Nullable
        @Override
        public String toString(@Nullable final ResultMap resultMap, final ConvertContext context) {
            return resultMap == null ? null : resultMap.getIdValue().orElse(null);
        }
    }

    /**
     * extends 属性
     */
    public static class Extends extends Base {
        @Nullable
        @Override
        public Collection<ResultMap> getVariants(ConvertContext context, Mapper mapper) {
            ResultMap domElement = (ResultMap) DomUtils.getDomElement(context.getTag());
            if (domElement == null) {
                return null;
            }
            return mapper.getResultMaps().stream()
                    .filter(resultMap -> domElement.getIdValue().map(id -> !id.equals(resultMap.getIdValue().orElse(null))).orElse(false))
                    .collect(Collectors.toList());
        }

    }

    /**
     * resultMap 属性
     *
     * @author : zhengrf
     * @date : 2019-07-02
     */
    public static class Attribute extends Base {

        @Nullable
        @Override
        protected Collection<ResultMap> getVariants(ConvertContext context, Mapper mapper) {
            return mapper.getResultMaps().stream().filter(resultMap -> resultMap.getId() != null).collect(Collectors.toList());
        }

    }
}
