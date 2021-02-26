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

package tk.cofe.plugin.mbl.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbl.psi.MblPsiCompositeElement;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public abstract class MblPsiCompositeElementBase extends ASTWrapperPsiElement implements MblPsiCompositeElement {
    public MblPsiCompositeElementBase(@NotNull final ASTNode node) {
        super(node);
    }
    @Override
    public String toString() {
        return getNode().getElementType().toString();
    }
}
