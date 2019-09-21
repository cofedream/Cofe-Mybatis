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

package tk.cofe.plugin.mybatis.dom.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.refactoring.rename.RenameUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.psi.MapperReferenceContributor;

import java.util.Collections;

/**
 * @author : zhengrf
 * @date : 2019-01-05
 * @see MapperReferenceContributor
 */
public class PsiIdentifierReference extends PsiReferenceBase.Poly<PsiIdentifier> {

    public PsiIdentifierReference(@NotNull PsiIdentifier element) {
        super(element);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return new ResolveResult[] {new PsiElementResolveResult(myElement)};
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        return new TextRange(1, myElement.getTextLength());
    }

    /**
     * 处理元素重命名
     */
    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        PsiElement element = myElement.getParent();
        if (element instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) element;
            UsageInfo[] methodUsage = RenameUtil.findUsages(psiMethod, newElementName, true, true, Collections.singletonMap(psiMethod, psiMethod.getName()));
            if (!ArrayUtil.isEmpty(methodUsage)) {
                RenamePsiElementProcessor processor = RenamePsiElementProcessor.forElement(psiMethod);
                processor.renameElement(psiMethod, newElementName, methodUsage, null);
            }
        }
        return element;
    }
}
