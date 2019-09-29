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

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * JdbcType 属性提示
 *
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class JdbcTypeConverter extends ResolvingConverter<String> {
    private static final Set<String> JdbcType = new HashSet<>(Arrays.asList(
            "BIT", "FLOAT", "CHAR", "TIMESTAMP", "OTHER", "UNDEFINED",
            "TINYINT", "REAL", "VARCHAR", "BINARY", "BLOB", "NVARCHAR",
            "SMALLINT", "DOUBLE", "LONGVARCHAR", "VARBINARY", "CLOB", "NCHAR",
            "INTEGER", "NUMERIC", "DATE", "LONGVARBINARY", "BOOLEAN", "NCLOB",
            "BIGINT", "DECIMAL", "TIME", "NULL", "CURSOR", "ARRAY"));

    @NotNull
    @Override
    public Collection<? extends String> getVariants(ConvertContext context) {
        return JdbcType;
    }

    @Nullable
    @Override
    public String fromString(@Nullable String s, ConvertContext context) {
        return JdbcType.contains(s) ? s : null;
    }

    @Nullable
    @Override
    public String toString(@Nullable String s, ConvertContext context) {
        return JdbcType.contains(s) ? s : null;
    }

    @Nullable
    @Override
    public LookupElement createLookupElement(final String s) {
        return s == null ? null : LookupElementBuilder.create(s);
    }
}
