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
package tk.cofe.plugin.mbel.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;

public class MbELVisitor extends PsiElementVisitor {

  public void visitJavaTypeConfig(@NotNull MbELJavaTypeConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitJdbcTypeConfig(@NotNull MbELJdbcTypeConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitJdbcTypeNameConfig(@NotNull MbELJdbcTypeNameConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitModeConfig(@NotNull MbELModeConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitNumericScaleConfig(@NotNull MbELNumericScaleConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitParamConfigList(@NotNull MbELParamConfigList o) {
    visitPsiCompositeElement(o);
  }

  public void visitReferenceExpression(@NotNull MbELReferenceExpression o) {
    visitPsiCompositeElement(o);
  }

  public void visitResultMapConfig(@NotNull MbELResultMapConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitTypeHandlerConfig(@NotNull MbELTypeHandlerConfig o) {
    visitPsiCompositeElement(o);
  }

  public void visitPsiCompositeElement(@NotNull MbELPsiCompositeElement o) {
    visitElement(o);
  }

}
