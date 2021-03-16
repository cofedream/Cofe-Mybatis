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

package tk.cofe.plugin.mbel.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbel.psi.MbELReferenceExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MbELPsiUtil {

    @NotNull
    public static PsiReference[] getReferences(MbELReferenceExpression element) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(element);
    }

    @NotNull
    public static PsiElement[] getChildren(MbELReferenceExpression element) {
        PsiElement psiChild = element.getFirstChild();
        if (psiChild == null) return PsiElement.EMPTY_ARRAY;

        List<PsiElement> result = new ArrayList<>();
        while (psiChild != null) {
            if (psiChild.getNode() instanceof MbELTokenImpl) {
                result.add(psiChild);
            }
            psiChild = psiChild.getNextSibling();
        }
        return PsiUtilCore.toPsiElementArray(result);
    }

}
