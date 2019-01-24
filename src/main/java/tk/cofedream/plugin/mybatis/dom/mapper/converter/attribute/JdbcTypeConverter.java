package tk.cofedream.plugin.mybatis.dom.mapper.converter.attribute;

import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
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
        if (JdbcType.contains(s)) {
            return s;
        }
        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable String s, ConvertContext context) {
        return s;
    }
}