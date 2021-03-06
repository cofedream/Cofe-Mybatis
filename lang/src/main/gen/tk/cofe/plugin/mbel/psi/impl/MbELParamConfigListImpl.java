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
package tk.cofe.plugin.mbel.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import tk.cofe.plugin.mbel.psi.*;

public class MbELParamConfigListImpl extends MbELPsiCompositeElementBase implements MbELParamConfigList {

  public MbELParamConfigListImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MbELVisitor visitor) {
    visitor.visitParamConfigList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MbELVisitor) accept((MbELVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<MbELJavaTypeConfig> getJavaTypeConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbELJavaTypeConfig.class);
  }

  @Override
  @NotNull
  public List<MbELJdbcTypeConfig> getJdbcTypeConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbELJdbcTypeConfig.class);
  }

  @Override
  @NotNull
  public List<MbELJdbcTypeNameConfig> getJdbcTypeNameConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbELJdbcTypeNameConfig.class);
  }

  @Override
  @NotNull
  public List<MbELModeConfig> getModeConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbELModeConfig.class);
  }

  @Override
  @NotNull
  public List<MbELNumericScaleConfig> getNumericScaleConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbELNumericScaleConfig.class);
  }

  @Override
  @NotNull
  public List<MbELResultMapConfig> getResultMapConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbELResultMapConfig.class);
  }

  @Override
  @NotNull
  public List<MbELTypeHandlerConfig> getTypeHandlerConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbELTypeHandlerConfig.class);
  }

}
