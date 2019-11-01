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

package tk.cofe.plugin.mybatis.dom.description;

import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.icons.MybatisIcons;
import tk.cofe.plugin.mybatis.dom.model.Configuration;

import javax.swing.*;

/**
 * 标注 mybatis configuration XML 文件,<a href="https://www.jetbrains.org/intellij/sdk/docs/reference_guide/frameworks_and_external_apis/xml_dom_api.html">详情</a>
 * @author : zhengrf
 * @date : 2019-01-02
 */
public class ConfigurationXmlDescription extends DomFileDescription<Configuration> {

    public ConfigurationXmlDescription() {
        super(Configuration.class, Configuration.TAG_NAME);
    }

    @Override
    protected void initializeFileDescription() {
        registerNamespacePolicy(Configuration.NAMESPACE_KEY, Configuration.DTDS);
    }

    @Nullable
    @Override
    public Icon getFileIcon(int flags) {
        return MybatisIcons.MybatisInterface;
    }
}
