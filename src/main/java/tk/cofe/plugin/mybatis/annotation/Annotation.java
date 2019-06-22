package tk.cofe.plugin.mybatis.annotation;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Mybatis相关注解
 * @author : zhengrf
 * @date : 2019-06-18
 */
public class Annotation implements Cloneable {

    public static final Annotation PARAM = new Annotation("@Param", "org.apache.ibatis.annotations.Param");

    public static final Annotation SELECT = new Annotation("@Select", "org.apache.ibatis.annotations.Select");

    public static final Annotation UPDATE = new Annotation("@Update", "org.apache.ibatis.annotations.Update");

    public static final Annotation INSERT = new Annotation("@Insert", "org.apache.ibatis.annotations.Insert");

    public static final Annotation DELETE = new Annotation("@Delete", "org.apache.ibatis.annotations.Delete");

    public static final Annotation ALIAS = new Annotation("@Alias", "org.apache.ibatis.type.Alias");

    public static final Annotation AUTOWIRED = new Annotation("@Autowired", "org.springframework.beans.factory.annotation.Autowired");

    public static final Annotation RESOURCE = new Annotation("@Resource", "javax.annotation.Resource");

    public static final List<Annotation> STATEMENT_ANNOTATIONS = Arrays.asList(SELECT, UPDATE, INSERT, DELETE);

    private final String label;

    private final String qualifiedName;

    private Map<String, AnnotationValue> attributePairs;

    public interface AnnotationValue {
    }

    public static class StringValue implements AnnotationValue {

        private String value;

        public StringValue(@NotNull String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "\"" + value + "\"";
        }

    }

    public Annotation(@NotNull String label, @NotNull String qualifiedName) {
        this.label = label;
        this.qualifiedName = qualifiedName;
        attributePairs = Maps.newHashMap();
    }

    private Annotation addAttribute(String key, AnnotationValue value) {
        this.attributePairs.put(key, value);
        return this;
    }

    public Annotation withAttribute(@NotNull String key, @NotNull AnnotationValue value) {
        Annotation copy = this.clone();
        copy.attributePairs = Maps.newHashMap(this.attributePairs);
        return copy.addAttribute(key, value);
    }

    public Annotation withValue(@NotNull AnnotationValue value) {
        return withAttribute("value", value);
    }

    public Annotation withValue(@NotNull String value) {
        return withAttribute("value", new StringValue(value));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(label);
        if (CollectionUtils.notEmpty(attributePairs.entrySet())) {
            builder.append(setupAttributeText());
        }
        return builder.toString();
    }

    private String setupAttributeText() {
        return getSingleValue().orElseGet(this::getComplexValue);
    }

    private String getComplexValue() {
        StringBuilder builder = new StringBuilder("(");
        for (String key : attributePairs.keySet()) {
            builder.append(key);
            builder.append(" = ");
            builder.append(attributePairs.get(key).toString());
            builder.append(", ");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        return builder.toString();
    }

    private Optional<String> getSingleValue() {
        try {
            String value = Iterables.getOnlyElement(attributePairs.keySet());
            return Optional.of("(" + attributePairs.get(value).toString() + ")");
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @NotNull
    public String getLabel() {
        return label;
    }

    @NotNull
    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    protected Annotation clone() {
        try {
            return (Annotation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException();
        }
    }
}
