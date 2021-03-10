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

package tk.cofe.plugin.mybatis.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import tk.cofe.plugin.mognl.psi.MOgnlReferenceExpression;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * @author : zhengrf
 * @date : 2021-03-10
 */
public class MOgnlKeywordCompletionContributor extends CompletionContributor {

    public static final PsiElementPattern.Capture<PsiElement> REFERENCE_EXPRESSION = psiElement().inside(MOgnlReferenceExpression.class);

    public MOgnlKeywordCompletionContributor() {
        installParam();
    }

    private void installParam() {
        extend(CompletionType.BASIC, REFERENCE_EXPRESSION, new FirstKeywordCompletionProvider(MOgnlReferenceExpression.class));
    }
}
