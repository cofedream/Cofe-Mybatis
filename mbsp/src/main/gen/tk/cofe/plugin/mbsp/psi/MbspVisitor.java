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
package tk.cofe.plugin.mbsp.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class MbspVisitor extends PsiElementVisitor {

  public void visitBinaryExpression(@NotNull MbspBinaryExpression o) {
    visitExpression(o);
  }

  public void visitConditionalExpression(@NotNull MbspConditionalExpression o) {
    visitExpression(o);
  }

  public void visitExpression(@NotNull MbspExpression o) {
    visitPsiCompositeElement(o);
  }

  public void visitIndexedExpression(@NotNull MbspIndexedExpression o) {
    visitExpression(o);
  }

  public void visitLiteralExpression(@NotNull MbspLiteralExpression o) {
    visitExpression(o);
  }

  public void visitMethodCallExpression(@NotNull MbspMethodCallExpression o) {
    visitExpression(o);
  }

  public void visitParamConfigExpression(@NotNull MbspParamConfigExpression o) {
    visitExpression(o);
  }

  public void visitParameterList(@NotNull MbspParameterList o) {
    visitPsiCompositeElement(o);
  }

  public void visitParenthesizedExpression(@NotNull MbspParenthesizedExpression o) {
    visitExpression(o);
  }

  public void visitProjectionExpression(@NotNull MbspProjectionExpression o) {
    visitExpression(o);
  }

  public void visitReferenceExpression(@NotNull MbspReferenceExpression o) {
    visitExpression(o);
  }

  public void visitSelectionExpression(@NotNull MbspSelectionExpression o) {
    visitExpression(o);
  }

  public void visitUnaryExpression(@NotNull MbspUnaryExpression o) {
    visitExpression(o);
  }

  public void visitVariableExpression(@NotNull MbspVariableExpression o) {
    visitExpression(o);
  }

  public void visitPsiCompositeElement(@NotNull MbspPsiCompositeElement o) {
    visitElement(o);
  }

}
