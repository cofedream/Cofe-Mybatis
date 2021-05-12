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

package tk.cofe.plugin.mbel;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.icons.MybatisIcons;

import javax.swing.*;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MbELFileType extends LanguageFileType {

    public static final MbELFileType INSTANCE = new MbELFileType();

    private MbELFileType() {
        super(MbELLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "MbEL";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Mybatis(expression language)";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "mbel";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MybatisIcons.MAIN;
    }
}
