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

package tk.cofe.plugin.mybatis.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author : zhengrf
 * @date : 2019-09-20
 */
public class TypeAliasRegistry {

    private static final Map<String, Class<?>> TYPE_ALIASES;

    static {
        TYPE_ALIASES = new HashMap<>();
        registerAlias("string", String.class);

        registerAlias("byte", Byte.class);
        registerAlias("long", Long.class);
        registerAlias("short", Short.class);
        registerAlias("int", Integer.class);
        registerAlias("integer", Integer.class);
        registerAlias("double", Double.class);
        registerAlias("float", Float.class);
        registerAlias("boolean", Boolean.class);

        registerAlias("byte[]", Byte[].class);
        registerAlias("long[]", Long[].class);
        registerAlias("short[]", Short[].class);
        registerAlias("int[]", Integer[].class);
        registerAlias("integer[]", Integer[].class);
        registerAlias("double[]", Double[].class);
        registerAlias("float[]", Float[].class);
        registerAlias("boolean[]", Boolean[].class);

        registerAlias("_byte", Byte.class);
        registerAlias("_long", Long.class);
        registerAlias("_short", Short.class);
        registerAlias("_int", Integer.class);
        registerAlias("_integer", Integer.class);
        registerAlias("_double", Double.class);
        registerAlias("_float", Float.class);
        registerAlias("_boolean", Boolean.class);

        registerAlias("_byte[]", Byte[].class);
        registerAlias("_long[]", Long[].class);
        registerAlias("_short[]", Short[].class);
        registerAlias("_int[]", Integer[].class);
        registerAlias("_integer[]", Integer[].class);
        registerAlias("_double[]", Double[].class);
        registerAlias("_float[]", Float[].class);
        registerAlias("_boolean[]", Boolean[].class);

        registerAlias("date", Date.class);
        registerAlias("decimal", BigDecimal.class);
        registerAlias("bigdecimal", BigDecimal.class);
        registerAlias("biginteger", BigInteger.class);
        registerAlias("object", Object.class);

        registerAlias("date[]", Date[].class);
        registerAlias("decimal[]", BigDecimal[].class);
        registerAlias("bigdecimal[]", BigDecimal[].class);
        registerAlias("biginteger[]", BigInteger[].class);
        registerAlias("object[]", Object[].class);

        registerAlias("map", Map.class);
        registerAlias("hashmap", HashMap.class);
        registerAlias("list", List.class);
        registerAlias("arraylist", ArrayList.class);
        registerAlias("collection", Collection.class);
        registerAlias("iterator", Iterator.class);

        registerAlias("ResultSet", ResultSet.class);
    }

    private static void registerAlias(String alias, Class<?> value) {
        if (alias == null) {
            return;
        }
        String key = alias.toLowerCase(Locale.ENGLISH);
        if (TYPE_ALIASES.containsKey(key) && TYPE_ALIASES.get(key) != null && !TYPE_ALIASES.get(key).equals(value)) {
            return;
        }
        TYPE_ALIASES.put(key, value);
    }

    /**
     * @since 3.2.2
     */
    public static Map<String, Class<?>> getTypeAliases() {
        return Collections.unmodifiableMap(TYPE_ALIASES);
    }

    public static Class<?> getType(String aliase) {
        return TYPE_ALIASES.get(aliase);
    }

}
