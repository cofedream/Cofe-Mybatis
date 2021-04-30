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

package tk.cofe.plugin.mybatis.ui.settings;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author : zhengrf
 * @date : 2021-04-30
 */
public class MapperScanEdit extends DialogWrapper {
    private JPanel root;
    private JTextField text;

    public MapperScanEdit(String title) {
        super(true);
        setTitle(title);
        init();
    }

    public String getTextString() {
        if (text == null) {
            return "";
        }
        return text.getText();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return root;
    }
}
