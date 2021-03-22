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
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.utils.*;
import tk.cofe.plugin.mybatis.psi.FirstIdentifierReference;
import tk.cofe.plugin.mybatis.psi.IdentifierReference;

import java.util.*;

/**
 * @author : zhengrf
 * @date : 2021-03-18
 */
public class CollectionReferenceProvider extends PsiReferenceProvider {

    @Override
    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        if (element.getTextLength() <= 0) {
            return PsiReference.EMPTY_ARRAY;
        }
        return build(element).toArray(PsiReference.EMPTY_ARRAY);
    }

    static List<PsiReference> build(@NotNull final PsiElement element) {
        final PsiElement valueToken = PsiElementUtils.getSubElement(element, XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN);
        if (valueToken == null || valueToken.getTextLength() <= 0) {
            return Collections.emptyList();
        }
        final String tokenText = valueToken.getText();
        final String[] textArr = tokenText.split("\\.");
        // // 获取到第一个引用
        List<PsiReference> references = new ArrayList<>(textArr.length);
        int startOffset = 1;
        int endOffset = startOffset + textArr[0].length();
        final FirstIdentifierReference reference = new FirstIdentifierReference(textArr[0], element, TextRange.create(startOffset, endOffset), PsiTreeUtil.getParentOfType(element, XmlTag.class));
        references.add(reference);
        // 如果没有解析多个引用
        PsiElement psiElement = reference.resolve();
        if (psiElement != null) {
            for (int i = 1; i < textArr.length; i++) {
                final String child = textArr[i];
                startOffset = endOffset + 1;  // '.' 的长度
                endOffset = startOffset + child.length();
                //
                PsiType psiType = null;
                if (psiElement instanceof PsiMember) {
                    psiType = PsiJavaUtils.getPsiMemberType((PsiMember) psiElement);
                } else if (psiElement instanceof PsiParameter) {
                    psiType = ((PsiParameter) psiElement).getType();
                }
                // 如果能查询到引用
                psiElement = CompletionUtils.getTheMethodOrField(child, psiType);
                references.add(new IdentifierReference(element, TextRange.create(startOffset, endOffset), psiElement, psiType) {
                    @Override
                    protected Collection<PsiMember> getClassMember() {
                        return CompletionUtils.getTheMethodAndField(psiClass).values();
                    }
                });
            }
        }
        return references;
    }

}
