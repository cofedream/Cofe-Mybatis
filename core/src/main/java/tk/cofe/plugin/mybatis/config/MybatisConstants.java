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

package tk.cofe.plugin.mybatis.config;

import org.jetbrains.annotations.NonNls;

/**
 * 常量
 *
 * @author : zhengrf
 * @date : 2020-01-20
 */
public final class MybatisConstants {

    private MybatisConstants() {
    }

    /**
     * DOM-Namespace-Key for config.xml
     */
    @NonNls
    public static final String CONFIG_NAMESPACE_KEY = "configuration namespace";
    @NonNls
    public static final String CONFIG_DTD_ID = "-//mybatis.org//DTD Config 3.0//EN";
    @NonNls
    public static final String CONFIG_DTD_URL = "http://mybatis.org/dtd/mybatis-3-config.dtd";
    /**
     * All config.xml DTD-IDs/URIs.
     */
    @NonNls
    public static final String[] CONFIG_NAME_SPACES = {
            CONFIG_DTD_URL, CONFIG_DTD_ID
    };
    /**
     * DOM-Namespace-Key for mapper.xml
     */
    @NonNls
    public static final String MAPPER_NAMESPACE_KEY = "mapper namespace";

    @NonNls
    public static final String MAPPER_DTD_URL = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";

    @NonNls
    public static final String MAPPER_DTD_ID = "-//mybatis.org//DTD Mapper 3.0//EN";
    /**
     * All mapper.xml DTD-IDs/URIs.
     */
    @NonNls
    public static final String[] MAPPER_NAME_SPACES = {
            MAPPER_DTD_URL, MAPPER_DTD_ID
    };


}
