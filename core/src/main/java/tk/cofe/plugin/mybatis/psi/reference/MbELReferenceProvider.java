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
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.utils.CompletionUtils;
import tk.cofe.plugin.common.utils.PsiJavaUtils;
import tk.cofe.plugin.common.utils.PsiTypeUtils;
import tk.cofe.plugin.mybatis.util.MybatisXMLUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhengrf
 * @date : 2020-01-19
 */
public class MbELReferenceProvider extends ReferenceExpressionReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull final ProcessingContext context) {
        if (element.getTextLength() <= 0) {
            return PsiReference.EMPTY_ARRAY;
        }
        final PsiElement originElement = MybatisXMLUtils.getOriginElement(element);
        if (originElement == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        // 方法参数
        return build(element, originElement, element.getText().split("\\."))
                .toArray(PsiReference.EMPTY_ARRAY);
    }

    private List<PsiReference> build(@NotNull final PsiElement element, final PsiElement originElement, final String[] prefixArr) {
        List<PsiReference> references = new ArrayList<>(prefixArr.length);
        int offsetStart = 0;
        int offsetEnd = prefixArr[0].length();
        // 获取到第一个引用
        final List<? extends PsiElement> firstReferences = getTargetElement(prefixArr[0], originElement);
        references.add(new PsiReferenceBase.Poly<>(element, new TextRange(offsetStart, offsetEnd), false) {
            @Override
            public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
                return PsiElementResolveResult.createResults(firstReferences);
            }
        });
        if (firstReferences.size() == 1 && prefixArr.length > 1) {
            final PsiElement firstReference = firstReferences.get(0);
            PsiType psiType;
            if (firstReference instanceof PsiMember) {
                psiType = PsiJavaUtils.getPsiMemberType((PsiMember) firstReference);
            } else if (firstReference instanceof PsiParameter) {
                psiType = ((PsiParameter) firstReference).getType();
            } else {
                return references;
            }
            for (int i = 1; i < prefixArr.length; i++) {
                // 从第二个开始
                if (!PsiTypeUtils.isCustomType(psiType)) {
                    break;
                }
                final String text = prefixArr[i];
                offsetStart = offsetStart + 1 + offsetEnd; // textArr[i-1].
                offsetEnd = offsetStart + text.length();
                references.add(PsiReferenceBase.createSelfReference(element, new TextRange(offsetStart, offsetEnd), CompletionUtils.getTheGetMethodOrField(text, psiType)));
            }
        }
        return references;
    }

}
