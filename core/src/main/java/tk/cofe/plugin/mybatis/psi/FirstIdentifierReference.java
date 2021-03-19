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

package tk.cofe.plugin.mybatis.psi;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ArrayUtil;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.utils.PsiMethodUtils;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2021-03-06
 */
public class FirstIdentifierReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private static final FirstElementSearchProvider<LookupElementBuilder> VARIANTS_PROVIDER = new FirstElementSearchProvider<>() {
        @Nonnull
        @Override
        public LookupElementBuilder mapper(String name, XmlAttributeValue xmlAttributeValue) {
            return LookupElementBuilder.create(xmlAttributeValue, xmlAttributeValue.getValue())
                    .withIcon(PlatformIcons.XML_TAG_ICON);
        }

        @Nonnull
        @Override
        public LookupElementBuilder mapper(String name, PsiParameter psiParameter) {
            return LookupElementBuilder.create(psiParameter, name)
                    .withIcon(PlatformIcons.PARAMETER_ICON);
        }

        @Nonnull
        @Override
        public LookupElementBuilder mapper(String name, PsiMember member) {
            if (member instanceof PsiMethod) {
                return createLookup(((PsiMethod) member));
            }
            if (member instanceof PsiField) {
                return createLookup(((PsiField) member));
            }
            return LookupElementBuilder.create(member, name);
        }

        @Nonnull
        private LookupElementBuilder createLookup(PsiField psiField) {
            return LookupElementBuilder.create(psiField, psiField.getName())
                    .withIcon(PlatformIcons.FIELD_ICON)
                    // .withTailText(psiField.getName())
                    .withPresentableText(psiField.getName())
                    .withTypeText(psiField.getType().getPresentableText())
                    ;
        }

        @Nonnull
        private LookupElementBuilder createLookup(PsiMethod psiMethod) {
            String methodName = PsiMethodUtils.replaceGetPrefix(psiMethod);
            return LookupElementBuilder.create(psiMethod, methodName)
                    // .withTypeText(psiMethod.getName())
                    .withIcon(PlatformIcons.METHOD_ICON)
                    .withPresentableText(methodName)
                    .withTailText(psiMethod.getName())
                    .withTypeText(Optional.ofNullable(psiMethod.getReturnType()).map(PsiType::getPresentableText).orElse(""))
                    ;
        }
    };
    private static final SuffixElementProvider<PsiElement> RESOLVE_PROVIDER = new SuffixElementProvider<>() {
        @Nonnull
        @Override
        public PsiElement mapper(PsiElement injectElement, XmlAttributeValue xmlAttributeValue) {
            return xmlAttributeValue;
        }

        @Nonnull
        @Override
        public PsiElement mapper(PsiElement injectElement, PsiParameter psiParameter) {
            return psiParameter;
        }

        @Nonnull
        @Override
        public PsiElement mapper(PsiElement injectElement, String name, PsiElement targetElement, @Nullable PsiType type) {
            return targetElement;
        }
    };
    private final Object[] variants;
    private final ResolveResult[] resolveResults;

    public FirstIdentifierReference(String name, @NotNull PsiElement element, final TextRange rangeInElement, PsiElement searchElement) {
        super(element, rangeInElement);
        this.variants = VARIANTS_PROVIDER.getTargetElement(searchElement).toArray(LookupElementBuilder.EMPTY_ARRAY);
        this.resolveResults = PsiElementResolveResult.createResults(RESOLVE_PROVIDER.getTargetElement(name, myElement, searchElement));
    }

    @Override
    public Object @NotNull [] getVariants() {
        return variants;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return resolveResults;
    }

}
