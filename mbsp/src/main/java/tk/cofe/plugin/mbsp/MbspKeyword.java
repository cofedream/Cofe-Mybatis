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

package tk.cofe.plugin.mbsp;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2021-02-20
 */
public final class MbspKeyword {
    private MbspKeyword() {
    }

    public static final class Mode {
        public static final String IN = "IN";
        public static final String OUT = "OUT";
        public static final String INOUT = "INOUT";
        public static final String[] STRINGS = {IN, OUT, INOUT};
    }

    public enum JdbcType {

        BIT,        FLOAT,       CHAR,          TIMESTAMP,      OTHER,    UNDEFINED,
        TINYINT,    REAL,        VARCHAR,       BINARY,         BLOB,     NVARCHAR,
        SMALLINT,   DOUBLE,      LONGVARCHAR,   VARBINARY,      CLOB,     NCHAR,
        INTEGER,    NUMERIC,     DATE,          LONGVARBINARY,  BOOLEAN,  NCLOB,
        BIGINT,     DECIMAL,     TIME,          NULL,           CURSOR,   ARRAY,
        ;
        public static final String[] STRINGS = Arrays.stream(values()).map(Enum::toString).collect(Collectors.toList()).toArray(new String[0]);
    }
}
