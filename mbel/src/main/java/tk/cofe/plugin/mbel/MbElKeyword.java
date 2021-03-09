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

package tk.cofe.plugin.mbel;

import java.util.Arrays;

/**
 * @author : zhengrf
 * @date : 2021-02-20
 */
public final class MbElKeyword {
    private MbElKeyword() {
    }

    public static final String MODE = "mode";
    public static final String JAVA_TYPE = "javaType";
    public static final String JDBC_TYPE = "jdbcType";
    public static final String JDBC_TYPE_NAME = "jdbcTypeName";
    public static final String NUMERIC_SCALE = "numericScale";
    public static final String TYPE_HANDLER = "typeHandler";
    public static final String RESULT_MAP = "resultMap";

    public static final String[] STRINGS = {MODE, JAVA_TYPE, JDBC_TYPE, JDBC_TYPE_NAME, NUMERIC_SCALE, TYPE_HANDLER, RESULT_MAP};

    public enum Mode {
        IN,
        OUT,
        INOUT,
        ;
        public static final String[] STRINGS = getStrings(values());
    }

    public enum JdbcType {

        BIT, FLOAT, CHAR, TIMESTAMP, OTHER, UNDEFINED,
        TINYINT, REAL, VARCHAR, BINARY, BLOB, NVARCHAR,
        SMALLINT, DOUBLE, LONGVARCHAR, VARBINARY, CLOB, NCHAR,
        INTEGER, NUMERIC, DATE, LONGVARBINARY, BOOLEAN, NCLOB,
        BIGINT, DECIMAL, TIME, NULL, CURSOR, ARRAY,
        ;
        public static final String[] STRINGS = getStrings(values());

    }

    private static String[] getStrings(Enum<?>[] enums) {
        return Arrays.stream(enums).map(Enum::toString).toArray(String[]::new);
    }
}
