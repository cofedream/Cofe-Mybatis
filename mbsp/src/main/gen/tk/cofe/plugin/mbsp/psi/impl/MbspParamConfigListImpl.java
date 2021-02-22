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

public class MbspParamConfigListImpl extends MbspPsiCompositeElementBase implements MbspParamConfigList {

  public MbspParamConfigListImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull MbspVisitor visitor) {
    visitor.visitParamConfigList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof MbspVisitor) accept((MbspVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<MbspJavaTypeConfig> getJavaTypeConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbspJavaTypeConfig.class);
  }

  @Override
  @NotNull
  public List<MbspJdbcTypeConfig> getJdbcTypeConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbspJdbcTypeConfig.class);
  }

  @Override
  @NotNull
  public List<MbspJdbcTypeNameConfig> getJdbcTypeNameConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbspJdbcTypeNameConfig.class);
  }

  @Override
  @NotNull
  public List<MbspModeConfig> getModeConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbspModeConfig.class);
  }

  @Override
  @NotNull
  public List<MbspNumericScaleConfig> getNumericScaleConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbspNumericScaleConfig.class);
  }

  @Override
  @NotNull
  public List<MbspResultMapConfig> getResultMapConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbspResultMapConfig.class);
  }

  @Override
  @NotNull
  public List<MbspTypeHandlerConfig> getTypeHandlerConfigList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, MbspTypeHandlerConfig.class);
  }

}
