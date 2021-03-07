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
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ArrayUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.psi.ResultMapReference;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
            return resultMap == null ? null : resultMap.getIdValue(null);
        }

        @Nullable
        @Override
        public PsiElement resolve(final ResultMap o, final ConvertContext context) {
            return o == null ? null : o.getId().getXmlAttributeValue();
        }

    }

    /**
     * ID引
     */
    public static class IdReferencing implements CustomReferenceConverter {
        private static final ResultMapReference RESULT_MAP_REFERENCE = new ResultMapReference((xmlAttributeValue, element) -> PsiReferenceBase.createSelfReference(element, xmlAttributeValue));

        @NotNull
        @Override
        public PsiReference[] createReferences(final GenericDomValue value, final PsiElement element, final ConvertContext context) {
            String rawText = value.getRawText();
            Optional<Mapper> mapperFile = MybatisUtils.getMapper(((XmlAttributeValue) element));
            // resultMap extends
            PsiReference[] extendsReference = mapperFile.map(RESULT_MAP_REFERENCE.getResultMapFunction(element, rawText)).orElse(new PsiReference[0]);
            // select resultMap
            PsiReference[] psiReferences = mapperFile.map(RESULT_MAP_REFERENCE.getSelectFunction(element, rawText)).orElse(new PsiReference[0]);
            // resultMap inside resultMap Attributes
            PsiReference[] resultMapAttributes = mapperFile.map(RESULT_MAP_REFERENCE.getResultMapAttributeFunction(element, rawText)).orElse(new PsiReference[0]);
            return ArrayUtil.mergeArrays(ArrayUtil.mergeArrays(extendsReference, psiReferences), resultMapAttributes);
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
