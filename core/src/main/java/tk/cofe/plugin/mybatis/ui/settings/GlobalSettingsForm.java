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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.settings.model.ApplicationSettings;
import tk.cofe.plugin.mybatis.settings.model.MapperScan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author : zhengrf
 * @date : 2021-04-30
 */
public class GlobalSettingsForm {

    private static final Logger LOGGER = Logger.getInstance(GlobalSettingsForm.class);

    private DefaultListModel<MapperScan> mapperScanModel;

    private JPanel root;

    private JPanel globalPanel;
    private JBList<MapperScan> mapperScanList;

    public GlobalSettingsForm(ApplicationSettings settings) {
        final ApplicationSettings clone = settings.clone();
        mapperScanModel = new DefaultListModel<>();
        for (int i = 0; i < clone.getMapperScanList().size(); i++) {
            mapperScanModel.add(i, clone.getMapperScanList().get(i));
        }
        mapperScanList = createJbList(mapperScanModel);
        globalPanel.add(ToolbarDecorator.createDecorator(mapperScanList)
                .setAddAction(anActionButton -> {
                    final MapperScanEdit edit = new MapperScanEdit("new MapperScan");
                    if (edit.showAndGet()) {
                        final String text = edit.getTextString();
                        if (StringUtil.isNotEmpty(text)) {
                            mapperScanModel.addElement(new MapperScan(text.trim()));
                        }
                    }
                })
                .setEditAction(anActionButton -> {
                    final MapperScan selectedValue = mapperScanList.getSelectedValue();
                    final MapperScanEdit edit = new MapperScanEdit("edit MapperScan", selectedValue.getCanonicalName());
                    if (edit.showAndGet()) {
                        final String text = edit.getTextString();
                        if (StringUtil.isNotEmpty(text)) {
                            selectedValue.setCanonicalName(text.trim());
                        }
                    }
                })
                .createPanel(), BorderLayout.CENTER);
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(@NotNull MouseEvent event) {
                final MapperScan selectedValue = mapperScanList.getSelectedValue();
                if (selectedValue == null) {
                    return false;
                }
                final MapperScanEdit edit = new MapperScanEdit("edit MapperScan", selectedValue.getCanonicalName());
                if (edit.showAndGet()) {
                    final String text = edit.getTextString();
                    if (StringUtil.isNotEmpty(text)) {
                        selectedValue.setCanonicalName(text.trim());
                    }
                }
                return true;
            }
        }.installOn(mapperScanList);
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
        return new ApplicationSettings(Collections.list(mapperScanModel.elements()));
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("MapperScan");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        globalPanel = new JPanel();
        globalPanel.setLayout(new BorderLayout(0, 0));
        panel1.add(globalPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
