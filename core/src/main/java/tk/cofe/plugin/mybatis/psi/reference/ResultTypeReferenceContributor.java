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
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.utils.*;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.JavaPsiService;

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

    /**
     * @author : zhengrf
     * @date : 2021-03-18
     */
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
            // if (valueToken == null || valueToken.getTextLength() <= 0) {
            //     // DomUtils.getDomElement(element, ClassElement.class)
            //     return Collections.emptyList();
            // }
            final JavaPsiService service = JavaPsiService.getInstance(element.getProject());
            return Collections.singletonList(Optional.ofNullable(valueToken)
                    // .filter(t -> t.getTextLength() <= 0)
                    .map(PsiElement::getText)
                    .map(TypeAliasUtils::getTypeName)
                    .flatMap(service::findPsiClass)
                    .map(psiClass -> new ResultTypeReference(element, TextRange.from(1, valueToken.getTextLength()), psiClass))
                    // .map(psiClass -> PsiReferenceBase.createSelfReference(element, TextRange.from(1, valueToken.getTextLength()), psiClass))
                    // .map(Collections::singletonList)
                    // .orElse(Collections.emptyList());
                    .orElse(new ResultTypeReference(element, TextRange.from(1, 0), null)));
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
                        String internalCanonicalText = type.getInternalCanonicalText();
                        PsiType targetType = type.getDeepComponentType();
                        if (PsiTypeUtils.isPrimitiveType(targetType)) {
                            builders.addAll(TypeAliasUtils.getTypeLookupElement(internalCanonicalText));
                        } else if (targetType instanceof PsiClassReferenceType) {
                            final PsiClass psiClass = ((PsiClassReferenceType) targetType).resolve();
                            builders.add(LookupElementBuilder.create(psiClass, targetType.getPresentableText())
                                    .withTypeText(internalCanonicalText)
                                    .withIcon(PlatformIcons.CLASS_ICON));
                        }
                        return builders.toArray(LookupElementBuilder.EMPTY_ARRAY);
                    }).orElse(LookupElementBuilder.EMPTY_ARRAY);
        }
    }
}
