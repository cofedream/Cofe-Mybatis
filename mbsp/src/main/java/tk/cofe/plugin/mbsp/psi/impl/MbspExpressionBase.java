/*
 * Copyright (C) 2019 cofe
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mbsp.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbsp.psi.MbspPsiCompositeElement;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public abstract class MbspExpressionBase extends ASTWrapperPsiElement implements MbspPsiCompositeElement, PsiNameIdentifierOwner {
    public MbspExpressionBase(@NotNull final ASTNode node) {
        super(node);
    }
}
