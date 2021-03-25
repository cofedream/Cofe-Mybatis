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

package tk.cofe.plugin.mybatis.psi.reference;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.*;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.TypeAliasService;

import java.util.*;

import static tk.cofe.plugin.mybatis.constant.ElementPattern.XML;

/**
 * @author : zhengrf
 * @date : 2020-01-19
 */
public class ResultTypeReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(XML.AttributeVlaue.RESULT_TYPE, new ResultTypeReferenceProvider());
    }

    public static class ResultTypeReferenceProvider extends PsiReferenceProvider {

        @Override
        public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            if (element.textContains('.')) {
                // 包含 '.'(视为JavaClassReference) 则不进行处理
                return PsiReference.EMPTY_ARRAY;
            }
            return build(element).toArray(PsiReference.EMPTY_ARRAY);
        }

        static List<? extends PsiReference> build(@NotNull final PsiElement element) {
            final PsiElement valueToken = PsiElementUtils.getSubElement(element, XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN);
            if (valueToken == null) {
                return Collections.emptyList();
            }
            return Optional.of(valueToken)
                    .map(PsiElement::getText)
                    .map(valueText -> {
                        final TypeAliasService typeAliasService = TypeAliasService.getInstance(element.getProject());
                        final Optional<PsiMethod> psiMethod = DomUtils.getDomElement(element, ClassElement.class)
                                .flatMap(ClassElement::getIdMethod)
                                .filter(info -> !PsiMethodUtils.isVoidMethod(info));
                        PsiElement psiElement;
                        if (typeAliasService.isPsiPrimitiveTypeAlias(valueText)) {
                            psiElement = psiMethod.map(PsiMethod::getReturnTypeElement).orElse(null);
                        } else {
                            psiElement = typeAliasService.getAliasPsiClass(valueText);
                            if (psiElement == null) {
                                // 从方法的returnType中获取
                                psiElement = psiMethod
                                        .map(PsiMethod::getReturnType)
                                        .filter(PsiClassType.class::isInstance)
                                        .map(PsiClassType.class::cast)
                                        .map(PsiClassType::resolve)
                                        .filter(psiClass -> {
                                            if (!PsiJavaUtils.hasAnnotation(psiClass, Annotation.ALIAS)) {
                                                return false;
                                            }
                                            final Annotation.Value value = Annotation.ALIAS.getValue(psiClass);
                                            if (value == null) {
                                                return false;
                                            }
                                            return Objects.equals(value.getValue(), valueText);
                                        }).orElse(null);
                            }
                        }
                        return new ResultTypeReference(element, TextRange.from(1, valueToken.getTextLength()), psiElement);
                    }).map(Collections::singletonList).orElse(Collections.emptyList());
        }
    }

    public static class ResultTypeReference extends PsiReferenceBase<PsiElement> {
        private final PsiElement myResolveTo;

        public ResultTypeReference(@NotNull PsiElement element, TextRange rangeInElement, PsiElement myResolveTo) {
            // 如果查询不到则不视为错误
            super(element, rangeInElement, true);
            this.myResolveTo = myResolveTo;
        }

        @Override
        public @Nullable PsiElement resolve() {
            return myResolveTo;
        }

        @Override
        public Object @NotNull [] getVariants() {
            final TypeAliasService instance = TypeAliasService.getInstance(myElement.getProject());
            return DomUtils.getDomElement(myElement, ClassElement.class)
                    .flatMap(ClassElement::getIdMethod)
                    .filter(info -> !PsiMethodUtils.isVoidMethod(info))
                    .map(PsiMethod::getReturnTypeElement)
                    .map(typeElement -> {
                        final PsiType type = typeElement.getType();
                        List<LookupElementBuilder> builders = new ArrayList<>();
                        PsiType targetType = type.getDeepComponentType();
                        if (PsiTypeUtils.isPrimitiveOrBoxType(targetType)) {
                            final String canonicalText = type.getCanonicalText();
                            for (String lookupString : instance.getTypeLookup(canonicalText)) {
                                builders.add(LookupElementBuilder.create(typeElement, lookupString)
                                        .withTypeText(canonicalText)
                                        .withIcon(PlatformIcons.METHOD_ICON)
                                        .bold());
                            }
                        } else if (targetType instanceof PsiClassType) {
                            final PsiClass psiClass = ((PsiClassType) targetType).resolve();
                            if (psiClass != null) {
                                String lookupString = targetType.getCanonicalText();
                                Annotation.Value value = Annotation.ALIAS.getValue(psiClass);
                                if (value != null && StringUtil.isNotEmpty(value.getValue())) {
                                    lookupString = value.getValue();
                                }
                                builders.add(LookupElementBuilder.create(psiClass, lookupString)
                                        .withPresentableText(targetType.getPresentableText())
                                        .withTypeText(type.getPresentableText())
                                        .withIcon(PlatformIcons.CLASS_ICON));
                            }
                        }
                        return builders.toArray(LookupElementBuilder.EMPTY_ARRAY);
                    }).orElse(LookupElementBuilder.EMPTY_ARRAY);
        }
    }
}
