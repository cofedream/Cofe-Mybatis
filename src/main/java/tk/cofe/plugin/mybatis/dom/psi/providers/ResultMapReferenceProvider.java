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

package tk.cofe.plugin.mybatis.dom.psi.providers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.ElementPresentationManager;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.convert.ResultMapConverter;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ResultMap Reference
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class ResultMapReferenceProvider {
    /**
     * Extends 属性引用
     * @see ResultMapConverter
     */
    public static class Extends extends PsiReferenceProvider {

        @NotNull
        @Override
        public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            return new PsiReference[] {new Reference(element)};
        }

        public static class Reference extends PsiReferenceBase.Poly<PsiElement> {

            public Reference(@NotNull PsiElement element) {
                super(element);
            }

            @NotNull
            @Override
            public ResolveResult[] multiResolve(boolean incompleteCode) {
                ResultMap domElement = (ResultMap) DomUtils.getDomElement(PsiTreeUtil.getParentOfType(myElement, XmlTag.class));
                if (domElement == null) {
                    return new ResolveResult[0];
                }
                Optional<String> extendsValue = domElement.getExtendsValue();
                if (!extendsValue.isPresent()) {
                    return new ResolveResult[0];
                }
                Mapper mapper = MybatisUtils.getMapper((XmlAttributeValue) myElement);
                if (mapper == null) {
                    return new ResolveResult[0];
                }
                List<ResolveResult> result = new ArrayList<>();
                mapper.getResultMaps().forEach(resultMap -> resultMap.getIdValue().ifPresent(id -> {
                    if (id.equals(extendsValue.get()) && resultMap.getId() != null && resultMap.getId().getXmlAttributeValue() != null) {
                        result.add(new PsiElementResolveResult(resultMap.getId().getXmlAttributeValue()));
                    }
                }));
                return result.toArray(new ResolveResult[0]);
            }

            @NotNull
            @Override
            public Object[] getVariants() {
                ResultMap domElement = (ResultMap) DomUtils.getDomElement(PsiTreeUtil.getParentOfType(myElement, XmlTag.class));
                if (domElement == null) {
                    return new Object[0];
                }
                Mapper mapper = MybatisUtils.getMapper((XmlAttributeValue) myElement);
                if (mapper == null) {
                    return new Object[0];
                }
                return ElementPresentationManager.getInstance().createVariants(mapper.getResultMaps()
                        .stream().filter(resultMap -> resultMap.getIdValue()
                                .map(otherId -> domElement.getIdValue()
                                        .map(selfId -> !otherId.equals(selfId))
                                        .orElse(false))
                                .orElse(false))
                        .collect(Collectors.toList()));
            }
        }
    }

    /**
     * Extends 属性引用
     */
    public static class Id extends PsiReferenceProvider {

        @NotNull
        @Override
        public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            return new PsiReference[] {new Reference(element)};
        }

        public static class Reference extends PsiReferenceBase.Poly<PsiElement> {

            public Reference(@NotNull PsiElement element) {
                super(element);
            }

            @NotNull
            @Override
            public ResolveResult[] multiResolve(boolean incompleteCode) {
                ResultMap domElement = (ResultMap) DomUtils.getDomElement(PsiTreeUtil.getParentOfType(myElement, XmlTag.class));
                if (domElement == null) {
                    return new ResolveResult[0];
                }
                Optional<String> idValue = domElement.getIdValue();
                if (!idValue.isPresent()) {
                    return new ResolveResult[0];
                }
                Mapper mapper = MybatisUtils.getMapper((XmlAttributeValue) myElement);
                if (mapper == null) {
                    return new ResolveResult[0];
                }
                List<ResolveResult> result = new ArrayList<>();
                mapper.getResultMaps().forEach(resultMap -> resultMap.getExtendsValue().ifPresent(extendsValue -> {
                    if (extendsValue.equals(idValue.get()) && resultMap.getXmlElement() != null) {
                        result.add(new PsiElementResolveResult(resultMap.getXmlElement()));
                    }
                }));
                return result.toArray(new ResolveResult[0]);
            }

        }
    }
}
