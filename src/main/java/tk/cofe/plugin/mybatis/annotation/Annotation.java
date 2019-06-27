package tk.cofe.plugin.mybatis.annotation;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

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
    private Value value;

    private Annotation(@NotNull String label, @NotNull String qualifiedName) {
        this.label = label;
        this.qualifiedName = qualifiedName;
    }

    public Annotation withValue(@NotNull String value) {
        Annotation copy = this.clone();
        copy.value = new Value(value);
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(label).append("(");
        if (value != null) {
            builder.append(value.toString());
        }
        return builder.append(")").toString();
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
        Annotation annotation = null;
        try {
            annotation = (Annotation) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return annotation;
    }

    private static class Value {

        private String value;

        Value(@NotNull String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "\"" + value + "\"";
        }

    }
}
