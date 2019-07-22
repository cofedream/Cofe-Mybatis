/*
 * Copyright (C) 2019 cofe
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

package tk.cofe.plugin.mybatis.dom.config;

import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.constants.Mybatis;
import tk.cofe.plugin.mybatis.dom.config.model.ConfigurationXml;
import tk.cofe.plugin.mybatis.icons.MybatisIcons;

import javax.swing.*;

/**
 * 标注 mybatis configuration XML 文件
 * @author : zhengrf
 * @date : 2019-01-02
 */
public class ConfigurationXmlDescription extends DomFileDescription<ConfigurationXml> {

    public ConfigurationXmlDescription() {
        super(ConfigurationXml.class, ConfigurationXml.TAG_NAME);
    }

    @Override
    protected void initializeFileDescription() {
        registerNamespacePolicy(Mybatis.Config.NAMESPACE_KEY, Mybatis.Config.DTDS);
    }

    @Nullable
    @Override
    public Icon getFileIcon(int flags) {
        return MybatisIcons.MybatisInterface;
    }
}
