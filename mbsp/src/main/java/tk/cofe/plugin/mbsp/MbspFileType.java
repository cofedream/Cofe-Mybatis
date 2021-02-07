/*
 * Copyright (C) 2019 cofe
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mbsp;

import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.AtomicNotNullLazyValue;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.ui.LayeredIcon;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.icons.MybatisIcons;

import javax.swing.*;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MbspFileType extends LanguageFileType {

    public static final MbspFileType INSTANCE = new MbspFileType();

    private MbspFileType() {
        super(MbspLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "MBSP";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Mybatis Navigation Language script";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "mbsp";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MybatisIcons.MAIN;
    }
}
