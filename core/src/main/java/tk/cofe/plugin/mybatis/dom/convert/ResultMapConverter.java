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

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.attirubte.IdAttribute;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.dom.model.tag.Association;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        protected List<ResultMap> getReferenceDomElements(String value, ConvertContext context, Mapper mapper) {
            return mapper.getResultMaps();
        }

        @Override
        protected boolean filterDomElement(ResultMap targetDomElement, String selfValue) {
            return targetDomElement.isEqualsId(selfValue);
        }

        @Nullable
        @Override
        public String toString(@Nullable final ResultMap resultMap, final ConvertContext context) {
            return Optional.ofNullable(resultMap)
                    .flatMap(IdAttribute::getIdValue)
                    .orElse(null);
        }

        @Nullable
        @Override
        public PsiElement resolve(final ResultMap o, final ConvertContext context) {
            return Optional.ofNullable(o)
                    .map(ResultMap::getId)
                    .map(GenericAttributeValue::getXmlAttributeValue)
                    .orElse(null);
        }

    }

    /**
     * id引用查询
     */
    public static class IdReferencing implements CustomReferenceConverter<String> {

        @NotNull
        @Override
        public PsiReference[] createReferences(final GenericDomValue<String> value, final PsiElement element, final ConvertContext context) {
            String rawText = value.getRawText();
            return MybatisUtils.getMapper(((XmlElement) element))
                    .map(mapper -> {
                        List<PsiReference> ref = new ArrayList<>();
                        // 所有的select 标签中的 resultMap属性
                        ref.addAll(mapper.getSelects().stream()
                                .map(ResultMapAttribute::getResultMap)
                                .filter(resultMap -> Objects.equals(rawText, DomUtils.getAttributeValue(resultMap)))
                                .map(GenericAttributeValue::getXmlAttributeValue)
                                .map(val -> PsiReferenceBase.createSelfReference(element, val))
                                .collect(Collectors.toList()));
                        // 除了当前的其他的resultMap中的 extends属性
                        ref.addAll(mapper.getResultMaps().stream()
                                .filter(resultMap -> !resultMap.isEqualsId(rawText)) // 与当前id不同的resultMap
                                .flatMap(resultMap -> {
                                    final Stream.Builder<GenericAttributeValue<?>> builder = Stream.builder();
                                    builder.accept(resultMap.getExtends());
                                    process(resultMap.getAssociations(), resultMap.getCollections(), builder);
                                    return builder.build();
                                })
                                .filter(e -> Objects.equals(rawText, DomUtils.getAttributeValue(e))) // extends等于当前的id
                                .map(GenericAttributeValue::getXmlAttributeValue)
                                .map(val -> PsiReferenceBase.createSelfReference(element, val))
                                .collect(Collectors.toList()));
                        return ref.toArray(PsiReference.EMPTY_ARRAY);
                    }).orElse(PsiReference.EMPTY_ARRAY);
        }

        private void process(List<Association> associations, List<tk.cofe.plugin.mybatis.dom.model.tag.dynamic.Collection> collections, Stream.Builder<GenericAttributeValue<?>> builder) {
            for (Association association : associations) {
                builder.accept(association.getResultMap());
                process(association.getAssociations(), association.getCollections(), builder);
            }
            for (tk.cofe.plugin.mybatis.dom.model.tag.dynamic.Collection collection : collections) {
                builder.accept(collection.getResultMap());
                process(collection.getAssociations(), collection.getCollections(), builder);
            }
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
                    .filter(resultMap -> !resultMap.isEqualsId(domElement.getIdValue(null)))
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
            return mapper.getResultMaps();
        }

    }
}
