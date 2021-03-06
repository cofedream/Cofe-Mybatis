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

import java.util.List;
import org.jetbrains.annotations.*;

public interface MbELParamConfigList extends MbELPsiCompositeElement {

  @NotNull
  List<MbELJavaTypeConfig> getJavaTypeConfigList();

  @NotNull
  List<MbELJdbcTypeConfig> getJdbcTypeConfigList();

  @NotNull
  List<MbELJdbcTypeNameConfig> getJdbcTypeNameConfigList();

  @NotNull
  List<MbELModeConfig> getModeConfigList();

  @NotNull
  List<MbELNumericScaleConfig> getNumericScaleConfigList();

  @NotNull
  List<MbELResultMapConfig> getResultMapConfigList();

  @NotNull
  List<MbELTypeHandlerConfig> getTypeHandlerConfigList();

}
