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

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import tk.cofe.plugin.mognl.psi.*;

public class MOgnlProjectionExpressionImpl extends MOgnlExpressionImpl implements MOgnlProjectionExpression {

  public MOgnlProjectionExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull MOgnlVisitor visitor) {
    visitor.visitProjectionExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MOgnlVisitor) accept((MOgnlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public MOgnlExpression getProjectionExpression() {
    return findNotNullChildByClass(MOgnlExpression.class);
  }

}
