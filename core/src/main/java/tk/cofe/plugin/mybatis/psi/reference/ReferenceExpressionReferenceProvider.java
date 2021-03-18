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

import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.utils.PsiJavaUtils;
import tk.cofe.plugin.mybatis.psi.FirstElementProvider;
import tk.cofe.plugin.mybatis.psi.IdentifierReference;
import tk.cofe.plugin.mybatis.util.MybatisXMLUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : zhengrf
 * @date : 2021-03-15
 */
abstract class ReferenceExpressionReferenceProvider extends PsiReferenceProvider {
    private static final FirstElementProvider<PsiElement> PROVIDER = new FirstElementProvider<>() {
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

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull final ProcessingContext context) {
        if (element.getTextLength() <= 0) {
            return PsiReference.EMPTY_ARRAY;
        }
        final PsiLanguageInjectionHost originElement = MybatisXMLUtils.getOriginElement(element);
        if (originElement == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        return build(element, originElement).toArray(PsiReference.EMPTY_ARRAY);
    }

    List<PsiReference> build(@NotNull final PsiElement element, final PsiElement originElement) {
        final PsiElement[] children = element.getChildren();
        if (ArrayUtil.isEmpty(children)) {
            return Collections.emptyList();
        }
        final PsiElement firstChild = children[0];
        List<PsiReference> references = new ArrayList<>(children.length);
        int startOffset = 0;
        int endOffset = firstChild.getTextLength();
        // 获取到第一个引用
        final List<? extends PsiElement> firstReferences = PROVIDER.getTargetElement(firstChild.getText(), originElement, originElement);
        references.add(new PsiReferenceBase.Poly<>(element, new TextRange(startOffset, endOffset), false) {
            @Override
            public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
                return PsiElementResolveResult.createResults(firstReferences);
            }
        });
        if (firstReferences.size() == 1) {
            PsiElement psiElement = firstReferences.get(0);
            for (int i = 1; i < children.length; i++) {
                PsiType psiType;
                if (psiElement instanceof PsiMember) {
                    psiType = PsiJavaUtils.getPsiMemberType((PsiMember) psiElement);
                } else if (psiElement instanceof PsiParameter) {
                    psiType = ((PsiParameter) psiElement).getType();
                } else {
                    return references;
                }
                final PsiElement child = children[i];
                if (isDOTElement(child)) {
                    endOffset += 1; // '.' 的长度
                    continue;
                }
                startOffset = endOffset;
                endOffset = startOffset + child.getTextLength();
                final String childText = child.getText();
                final PsiMember suffixElement = childText.contains(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED) ? null : findSuffixElement(childText, psiType);
                references.add(createReference(element, psiType, suffixElement, new TextRange(startOffset, endOffset)));
                psiElement = suffixElement;
            }
        }
        return references;
    }

    protected abstract boolean isDOTElement(PsiElement element);

    protected abstract PsiMember findSuffixElement(String text, PsiType psiType);

    @Nonnull
    protected abstract IdentifierReference createReference(@Nonnull PsiElement element, PsiType psiType, PsiMember suffixElement, final TextRange textRange);
}
