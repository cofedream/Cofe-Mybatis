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
package tk.cofe.plugin.mognl.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class MOgnlVisitor extends PsiElementVisitor {

  public void visitBinaryExpression(@NotNull MOgnlBinaryExpression o) {
    visitExpression(o);
  }

  public void visitConditionalExpression(@NotNull MOgnlConditionalExpression o) {
    visitExpression(o);
  }

  public void visitExpression(@NotNull MOgnlExpression o) {
    visitPsiCompositeElement(o);
  }

  public void visitIndexedExpression(@NotNull MOgnlIndexedExpression o) {
    visitExpression(o);
  }

  public void visitLiteralExpression(@NotNull MOgnlLiteralExpression o) {
    visitExpression(o);
  }

  public void visitMethodCallExpression(@NotNull MOgnlMethodCallExpression o) {
    visitExpression(o);
  }

  public void visitParameterList(@NotNull MOgnlParameterList o) {
    visitPsiCompositeElement(o);
  }

  public void visitParenthesizedExpression(@NotNull MOgnlParenthesizedExpression o) {
    visitExpression(o);
  }

  public void visitProjectionExpression(@NotNull MOgnlProjectionExpression o) {
    visitExpression(o);
  }

  public void visitReferenceExpression(@NotNull MOgnlReferenceExpression o) {
    visitExpression(o);
  }

  public void visitSelectionExpression(@NotNull MOgnlSelectionExpression o) {
    visitExpression(o);
  }

  public void visitUnaryExpression(@NotNull MOgnlUnaryExpression o) {
    visitExpression(o);
  }

  public void visitVariableExpression(@NotNull MOgnlVariableExpression o) {
    visitExpression(o);
  }

  public void visitPsiCompositeElement(@NotNull MOgnlPsiCompositeElement o) {
    visitElement(o);
  }

}
