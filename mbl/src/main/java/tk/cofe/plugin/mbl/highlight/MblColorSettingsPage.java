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

package tk.cofe.plugin.mbl.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.icons.MybatisIcons;

import javax.swing.*;
import java.util.Map;

/**
 * @author : zhengrf
 * @date : 2021-02-25
 */
public class MblColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] ATTRIBUTES_DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Parameter config keyword", MblSyntaxHighlighter.KEYWORDS),
    };

    @Override
    public @Nullable Icon getIcon() {
        return MybatisIcons.MAIN;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new MblSyntaxHighlighter();
    }

    @Override
    public @NonNls
    @NotNull String getDemoText() {
        return "#{demo,javaType=String}";
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return ATTRIBUTES_DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull @NlsContexts.ConfigurableName String getDisplayName() {
        return "Mybatis";
    }
}
