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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import tk.cofe.plugin.mybatis.settings.model.ApplicationSettings;
import tk.cofe.plugin.mybatis.settings.model.MapperScan;

import javax.swing.*;
import java.awt.*;

/**
 * @author : zhengrf
 * @date : 2021-04-30
 */
public class GlobalSettingsForm {
    private DefaultListModel<MapperScan> mapperScanModel;

    private JPanel root;

    private JPanel globalPanel;
    private JBList<MapperScan> mapperScanList;

    public GlobalSettingsForm(ApplicationSettings settings) {
        final ApplicationSettings clone = settings.clone();
        mapperScanModel = new DefaultListModel<>();
        mapperScanList = createJbList(mapperScanModel);
        globalPanel.add(ToolbarDecorator.createDecorator(mapperScanList)
                .setAddAction(anActionButton -> {
                    final MapperScanEdit edit = new MapperScanEdit("new MapperScan");
                    if (edit.showAndGet()) {
                        final String text = edit.getTextString();
                        if (StringUtil.isNotEmpty(text)) {
                            mapperScanModel.addElement(new MapperScan(text));
                        }
                    }
                })
                .createPanel(), BorderLayout.CENTER);
        for (MapperScan mapperScan : clone.getMapperScanList()) {
            mapperScanModel.addElement(mapperScan);
        }
    }

    private JBList<MapperScan> createJbList(final DefaultListModel<MapperScan> model) {
        final JBList<MapperScan> list = new JBList<>(model);
        list.setCellRenderer(new DefaultListCellRenderer() {

            private static final long serialVersionUID = -7848076635687289102L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                final Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                final MapperScan mapperScan = (MapperScan) value;
                setText(mapperScan.getCanonicalName());
                return component;
            }
        });
        list.setDragEnabled(true);
        list.setDropMode(DropMode.INSERT);
        return list;
    }

    public JPanel getRoot() {
        return root;
    }

    public boolean isSettingsModified(ApplicationSettings settings) {
        return !getSettings().equals(settings);
    }

    public ApplicationSettings getSettings() {
        return new ApplicationSettings(this.mapperScanList.getSelectedValuesList());
    }
}
