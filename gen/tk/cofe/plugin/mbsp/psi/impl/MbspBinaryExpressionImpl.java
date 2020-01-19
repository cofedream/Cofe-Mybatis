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

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mbsp.psi.MbspBinaryExpression;
import tk.cofe.plugin.mbsp.psi.MbspExpression;
import tk.cofe.plugin.mbsp.psi.MbspVisitor;

import java.util.List;

public class MbspBinaryExpressionImpl extends MbspExpressionImpl implements MbspBinaryExpression {

    public MbspBinaryExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull MbspVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof MbspVisitor) {
            accept((MbspVisitor) visitor);
        } else {
            super.accept(visitor);
        }
    }

    @Override
    @NotNull
    public MbspExpression getLeft() {
        List<MbspExpression> p1 = PsiTreeUtil.getChildrenOfTypeAsList(this, MbspExpression.class);
        return p1.get(0);
    }

    @Override
    @Nullable
    public MbspExpression getRight() {
        List<MbspExpression> p1 = PsiTreeUtil.getChildrenOfTypeAsList(this, MbspExpression.class);
        return p1.size() < 2 ? null : p1.get(1);
    }

}
