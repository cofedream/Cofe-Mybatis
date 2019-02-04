package tk.cofedream.plugin.mybatis.enums;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Java 类型枚举
 * @author : zhengrf
 * @date : 2019-01-29
 */
public enum JavaTypeEnum {
    // 空
    VOID() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "void";
        }
    },
    // 基本类型
    BOOLEAN() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "boolean";
        }
    },
    BYTE() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "byte";
        }
    },
    CHAR() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "char";
        }
    },
    DOUBLE() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "double";
        }
    },
    FLOAT() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "float";
        }
    },
    INT() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "int";
        }
    },
    LONG() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "int";
        }
    },
    SHORT() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "short";
        }
    },
    // 包装类
    Boolean() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "boolean";
        }
    },
    Byte() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "Byte";
        }
    },
    Character() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "Character";
        }
    },
    Double() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "Double";
        }
    },
    Float() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "Float";
        }
    },
    Integer() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "Integer";
        }
    },
    Long() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "Long";
        }
    },
    Short() {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "Short";
        }
    },
    // 其他Java内置类
    Object {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "Object";
        }
    },
    String {
        @Nullable
        @Override
        java.lang.String getKey() {
            return "String";
        }
    },
    Date {
        @Override
        boolean checkInstance(@NotNull PsiClass psiClass) {
            return false;
        }
    },
    Bigdecimal {},
    Iterator {},
    // 集合类型
    Map {
        @Nullable
        @Override
        Class getTypeClass() {
            return java.util.Map.class;
        }
    },
    List {
        @Nullable
        @Override
        Class getTypeClass() {
            return java.util.List.class;
        }
    },
    Set {
        @Nullable
        @Override
        Class getTypeClass() {
            return java.util.Set.class;
        }
    },
    // 自定义类型
    Custom() {},
    ;

    @NotNull
    public static JavaTypeEnum parse(@NotNull PsiType psiType) {
        for (JavaTypeEnum typeEnum : values()) {
            if (typeEnum.support(psiType)) {
                return typeEnum;
            }
        }
        return Custom;
    }

    boolean support(@NotNull PsiType psiType) {
        if (psiType.getPresentableText().equals(getKey())) {
            return true;
        }
        if (psiType instanceof PsiClassReferenceType) {
            PsiClass psiClass = ((PsiClassReferenceType) psiType).resolve();
            if (psiClass == null) {
                return false;
            }
            return checkInstance(psiClass);
        }
        return false;
    }

    @Nullable
    String getKey() {
        return null;
    }

    @SuppressWarnings("unchecked")
    boolean checkInstance(@NotNull PsiClass psiClass) {
        Class typeClass = getTypeClass();
        if (typeClass != null) {
            for (PsiClass aSuper : psiClass.getSupers()) {
                if (typeClass.getTypeName().equals(aSuper.getQualifiedName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    Class getTypeClass() {
        return null;
    }
}
