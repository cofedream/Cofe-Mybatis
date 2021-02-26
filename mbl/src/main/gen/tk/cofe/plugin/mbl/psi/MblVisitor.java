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
package tk.cofe.plugin.mbl.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class MblVisitor extends PsiElementVisitor {

  public void visitBinaryExpression(@NotNull MblBinaryExpression o) {
    visitExpression(o);
  }

  public void visitConditionalExpression(@NotNull MblConditionalExpression o) {
    visitExpression(o);
  }

  public void visitExpression(@NotNull MblExpression o) {
    visitPsiCompositeElement(o);
  }

  public void visitIndexedExpression(@NotNull MblIndexedExpression o) {
    visitExpression(o);
  }

  public void visitJavaTypeConfig(@NotNull MblJavaTypeConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitJdbcTypeConfig(@NotNull MblJdbcTypeConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitJdbcTypeNameConfig(@NotNull MblJdbcTypeNameConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitLiteralExpression(@NotNull MblLiteralExpression o) {
    visitExpression(o);
  }

  public void visitMethodCallExpression(@NotNull MblMethodCallExpression o) {
    visitExpression(o);
  }

  public void visitModeConfig(@NotNull MblModeConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitNumericScaleConfig(@NotNull MblNumericScaleConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitParamConfigList(@NotNull MblParamConfigList o) {
    visitPsiCompositeElement(o);
  }

  public void visitParameterList(@NotNull MblParameterList o) {
    visitPsiCompositeElement(o);
  }

  public void visitParenthesizedExpression(@NotNull MblParenthesizedExpression o) {
    visitExpression(o);
  }

  public void visitProjectionExpression(@NotNull MblProjectionExpression o) {
    visitExpression(o);
  }

  public void visitReferenceExpression(@NotNull MblReferenceExpression o) {
    visitExpression(o);
  }

  public void visitResultMapConfig(@NotNull MblResultMapConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitSelectionExpression(@NotNull MblSelectionExpression o) {
    visitExpression(o);
  }

  public void visitTypeHandlerConfig(@NotNull MblTypeHandlerConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitUnaryExpression(@NotNull MblUnaryExpression o) {
    visitExpression(o);
  }

  public void visitVariableExpression(@NotNull MblVariableExpression o) {
    visitExpression(o);
  }

  public void visitPsiCompositeElement(@NotNull MblPsiCompositeElement o) {
    visitElement(o);
  }

}
