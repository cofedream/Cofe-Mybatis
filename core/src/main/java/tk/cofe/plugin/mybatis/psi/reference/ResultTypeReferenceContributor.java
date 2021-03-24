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
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.utils.*;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.TypeAliasService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static tk.cofe.plugin.mybatis.constant.ElementPattern.XML;

/**
 * @author : zhengrf
 * @date : 2020-01-19
 */
public class ResultTypeReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(XML.RESULT_TYPE_PATTERN, new ResultTypeReferenceProvider());
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
                        final PsiElement psiElement;
                        if (typeAliasService.isPsiPrimitiveTypeAlias(valueText)) {
                            psiElement = DomUtils.getDomElement(element, ClassElement.class)
                                    .flatMap(ClassElement::getIdMethod)
                                    .filter(info -> !PsiMethodUtils.isVoidMethod(info))
                                    .map(PsiMethod::getReturnTypeElement)
                                    .orElse(null);
                        } else {
                            psiElement = typeAliasService.getAliasPsiClass(valueText);
                        }
                        return new ResultTypeReference(element, TextRange.from(1, valueToken.getTextLength()), psiElement);
                    }).map(Collections::singletonList).orElse(Collections.emptyList());
        }
    }

    public static class ResultTypeReference extends PsiReferenceBase<PsiElement> {
        private final PsiElement myResolveTo;

        public ResultTypeReference(@NotNull PsiElement element, TextRange rangeInElement, PsiElement myResolveTo) {
            super(element, rangeInElement, false);
            this.myResolveTo = myResolveTo;
        }

        @Override
        public @Nullable PsiElement resolve() {
            return myResolveTo;
        }

        @Override
        public Object @NotNull [] getVariants() {
            return DomUtils.getDomElement(myElement, ClassElement.class)
                    .flatMap(ClassElement::getIdMethod)
                    .filter(info -> !PsiMethodUtils.isVoidMethod(info))
                    .map(PsiMethod::getReturnType)
                    .map(type -> {
                        List<LookupElementBuilder> builders = new ArrayList<>();
                        PsiType targetType = type.getDeepComponentType();
                        if (PsiTypeUtils.isPrimitiveOrBoxType(targetType)) {
                            builders.addAll(TypeAliasRegister.getTypeLookupElement(type.getCanonicalText()));
                        } else if (targetType instanceof PsiClassType) {
                            final PsiClass psiClass = ((PsiClassType) targetType).resolve();
                            if (psiClass != null) {
                                builders.add(LookupElementBuilder.create(psiClass, targetType.getCanonicalText())
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
