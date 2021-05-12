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

package tk.cofe.plugin.mognl;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.icons.MybatisIcons;

import javax.swing.*;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MOgnlFileType extends LanguageFileType {

    public static final MOgnlFileType INSTANCE = new MOgnlFileType();
    public static final String DEFAULT_EXTENSION = "mognl";

    private MOgnlFileType() {
        super(MOgnlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "MOgnl";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Mybatis(OGNL)";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MybatisIcons.MAIN;
    }
}
