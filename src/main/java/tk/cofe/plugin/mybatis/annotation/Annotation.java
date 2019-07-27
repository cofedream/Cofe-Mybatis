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

package tk.cofe.plugin.mybatis.annotation;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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

    @Nullable
    public Value getValue(@NotNull PsiParameter psiParameter) {
        PsiAnnotation annotation = psiParameter.getAnnotation(this.qualifiedName);
        if (annotation != null) {
            String value = AnnotationUtil.getStringAttributeValue(annotation, Value.getName());
            if (value != null) {
                return new Value(value);
            }
        }
        return null;
    }

    @NotNull
    public Value getValue(@NotNull PsiParameter psiParameter, @NotNull Supplier<String> defaultValue) {
        Value value = getValue(psiParameter);
        return value == null ? new Value(defaultValue.get()) : value;
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

    public static class Value {

        private String value;

        private Value(@NotNull String value) {
            this.value = value;
        }

        public static String getName() {
            return "value";
        }

        @NotNull
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "\"" + value + "\"";
        }

    }
}
