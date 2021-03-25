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

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.utils.CompletionUtils;
import tk.cofe.plugin.common.utils.PsiElementUtils;
import tk.cofe.plugin.mbel.MbELTypes;
import tk.cofe.plugin.mybatis.psi.IdentifierReference;

import java.util.Collection;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author : zhengrf
 * @date : 2021-03-10
 */
public class MbELReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(psiElement(MbELTypes.REFERENCE_EXPRESSION), new MbELReferenceProvider());
    }

    /**
     * @author : zhengrf
     * @date : 2020-01-19
     */
    public static class MbELReferenceProvider extends ReferenceExpressionReferenceProvider {

        @Override
        protected boolean isDOTElement(PsiElement element) {
            return MbELTypes.DOT == PsiElementUtils.getElementType(element);
        }

        @Override
        protected IdentifierReference createReference(PsiElement element, PsiType psiType, PsiMember suffixElement, TextRange textRange) {
            return new IdentifierReference(element, textRange, suffixElement, psiType) {
                @Override
                protected Collection<PsiMember> getClassMember() {
                    return CompletionUtils.getTheGetMethodAndField(psiClass).values();
                }
            };
        }

        @Override
        protected PsiMember findSuffixElement(String text, PsiType psiType) {
            return CompletionUtils.getTheGetMethodOrField(text, psiType);
        }
    }
}
