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

package tk.cofe.plugin.mybatis.constants;

import org.jetbrains.annotations.NonNls;

/**
 * @author : zhengrf
 * @date : 2019-01-02
 */
public final class Mybatis {

    public static final class Mapper {

        @NonNls
        public static final String MAPPER_DTD_URL = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";
        @NonNls
        public static final String MAPPER_DTD_ID = "-//mybatis.org//DTD Mapper 3.0//EN";
        /**
         * All mapper.xml DTD-IDs/URIs.
         */
        @NonNls
        public static final String[] DTDS = {
                MAPPER_DTD_URL, MAPPER_DTD_ID
        };
        /**
         * DOM-Namespace-Key for mapper.xml
         */
        @NonNls
        public static final String NAMESPACE_KEY = "mapper namespace";
    }

    public static final class Config {

        @NonNls
        public static final String DTD_URL = "http://mybatis.org/dtd/mybatis-3-config.dtd";
        @NonNls
        public static final String DTD_ID = "-//mybatis.org//DTD Config 3.0//EN";
        /**
         * All config.xml DTD-IDs/URIs.
         */
        @NonNls
        public static final String[] DTDS = {
                DTD_URL, DTD_ID
        };
        /**
         * DOM-Namespace-Key for config.xml
         */
        @NonNls
        public static final String NAMESPACE_KEY = "configuration namespace";
    }

}
