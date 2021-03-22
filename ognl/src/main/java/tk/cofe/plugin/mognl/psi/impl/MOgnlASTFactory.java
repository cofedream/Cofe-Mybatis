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

package tk.cofe.plugin.mognl.psi.impl;

import com.intellij.lang.ASTFactory;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mognl.MOgnlTypes;

/**
 * @author : zhengrf
 * @date : 2021-03-16
 */
public class MOgnlASTFactory extends ASTFactory {

    @Override
    public LeafElement createLeaf(@NotNull final IElementType type, @NotNull CharSequence text) {
        if (type == MOgnlTypes.DOT || type == MOgnlTypes.IDENTIFIER) {
            return new MOgnlTokenImpl(type, text);
        }
        return null;
    }
}
