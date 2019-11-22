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

package tk.cofe.plugin.mybatis.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Namespace;
import org.jetbrains.annotations.NonNls;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
@Namespace(Configuration.NAMESPACE_KEY)
public interface Configuration extends DomElement {

    @NonNls
    String DTD_URL = "http://mybatis.org/dtd/mybatis-3-config.dtd";
    @NonNls
    String DTD_ID = "-//mybatis.org//DTD Config 3.0//EN";
    /**
     * All config.xml DTD-IDs/URIs.
     */
    @NonNls
    String[] DTDS = {
            DTD_URL, DTD_ID
    };
    /**
     * DOM-Namespace-Key for config.xml
     */
    @NonNls
    String NAMESPACE_KEY = "configuration namespace";
    String TAG_NAME = "configuration";
}
