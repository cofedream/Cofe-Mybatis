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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ArrayUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.attirubte.ResultMapAttribute;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.MybatisUtils;
import tk.cofe.plugin.mybatis.util.RecursionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
        protected List<ResultMap> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper) {
            return mapper.getResultMaps();
        }

        @Override
        protected boolean filterDomElement(@NotNull ResultMap targetDomElement, @NotNull String selfValue) {
            return targetDomElement.isEqualsId(selfValue);
        }

        @Nullable
        @Override
        public String toString(@Nullable final ResultMap resultMap, final ConvertContext context) {
            return resultMap == null ? null : resultMap.getIdValue(null);
        }

        @Nullable
        @Override
        public PsiElement resolve(final ResultMap o, final ConvertContext context) {
            if (o == null) {
                return super.resolve(o, context);
            }
            return o.getId().getXmlAttributeValue();
        }

        @Override
        public void bindReference(final GenericDomValue<ResultMap> genericValue, final ConvertContext context, final PsiElement newTarget) {
            super.bindReference(genericValue, context, newTarget);
        }

        @Override
        public boolean isReferenceTo(@NotNull final PsiElement element, final String stringValue, @Nullable final ResultMap resolveResult, final ConvertContext context) {
            return super.isReferenceTo(element, stringValue, resolveResult, context);
        }

    }

    /**
     * ID引
     */
    public static class IdReferencing implements CustomReferenceConverter {

        @NotNull
        @Override
        public PsiReference[] createReferences(final GenericDomValue value, final PsiElement element, final ConvertContext context) {
            String rawText = value.getRawText();
            Optional<Mapper> mapperFile = MybatisUtils.getMapper(((XmlAttributeValue) element));
            // resultMap extends
            PsiReference[] extendsReference = mapperFile.map(getResultMapFunction(element, rawText)).orElse(new PsiReference[0]);
            // select resultMap
            PsiReference[] psiReferences = mapperFile.map(getSelectFunction(element, rawText)).orElse(new PsiReference[0]);
            // resultMap inside resultMap Attributes
            PsiReference[] resultMapAttributes = mapperFile.map(getResultMapAttributeFunction(element, rawText)).orElse(new PsiReference[0]);
            return ArrayUtil.mergeArrays(ArrayUtil.mergeArrays(extendsReference, psiReferences), resultMapAttributes);
        }

        /**
         * 处理XML中 ResultMap标签
         */
        private Function<Mapper, PsiReference[]> getResultMapFunction(final PsiElement element, final String rawText) {
            return mapper -> processSteam(element, getResultMapStream(rawText, mapper).map(ResultMap::getExtends));
        }

        /**
         * 处理XML中 Select标签
         */
        private Function<Mapper, PsiReference[]> getSelectFunction(final PsiElement element, final String rawText) {
            return mapper -> processSteam(element, mapper.getSelects().stream()
                    .filter(select -> select.isTargetResultMapAttribute(rawText))
                    .map(ResultMapAttribute::getResultMap));
        }

        /**
         * 处理XML中 ResultMap标签内子标签的ResultMap属性
         */
        private Function<Mapper, PsiReference[]> getResultMapAttributeFunction(final PsiElement element, final String rawText) {
            return mapper -> processSteam(element, getResultMapStream(rawText, mapper)
                    .flatMap(resultMap -> RecursionUtils.recursionResultMapAttribute(resultMap).stream())
                    .filter(resultMapAttribute -> resultMapAttribute.isTargetResultMapAttribute(rawText))
                    .map(ResultMapAttribute::getResultMap));
        }

        private Stream<ResultMap> getResultMapStream(final String rawText, final Mapper mapper) {
            return mapper.getResultMaps().stream().filter(resultMap -> !resultMap.isEqualsId(rawText) && Objects.equals(rawText, resultMap.getExtendsValue().orElse(null)));
        }

        private PsiReference[] processSteam(final PsiElement element, final Stream<GenericAttributeValue<ResultMap>> stream) {
            return stream.map(GenericAttributeValue::getXmlAttributeValue)
                    .filter(Objects::nonNull)
                    .map(xmlAttributeValue -> new PsiReferenceBase.Immediate<>(element, xmlAttributeValue)).toArray(PsiReference[]::new);
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
