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
package tk.cofe.plugin.mbsp.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static tk.cofe.plugin.mbsp.MbspTypes.*;
import tk.cofe.plugin.mbsp.psi.*;

public class MbspMethodCallExpressionImpl extends MbspExpressionImpl implements MbspMethodCallExpression {

  public MbspMethodCallExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull MbspVisitor visitor) {
    visitor.visitMethodCallExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MbspVisitor) accept((MbspVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public MbspParameterList getParameterList() {
    return findChildByClass(MbspParameterList.class);
  }

  @Override
  @NotNull
  public List<MbspExpression> getMethod() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbspExpression.class);
  }

}
