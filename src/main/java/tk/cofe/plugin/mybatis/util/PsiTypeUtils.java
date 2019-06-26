package tk.cofe.plugin.mybatis.util;

import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : zhengrf
 * @date : 2019-01-08
 */
public final class PsiTypeUtils {

    private static final Collection<String> BASE_TYPE = Arrays.asList("byte", "short", "int", "long", "float", "double", "boolean", "char");
    private static final Collection<String> BOXED_BASE_TYPE = Arrays.asList("Byte", "Short", "Integer", "Long", "Float", "Double", "Boolean", "Character");
    private static final Collection<String> OTHER_TYPE = Arrays.asList("String", "Date", "Bigdecimal", "Object", "Iterator");
    private static final Collection<String> COLLECTION_TYPE = Arrays.asList("Map", "Hashmap", "TreeMap",
            "List", "ArrayList", "LinkedList",
            "Set", "HashSet", "TreeSet"
            , "Collection");

    public static boolean isJavaBuiltInType(@NotNull PsiType psiType) {
        // 获取显示的文本内容 Boolean/List<String>
        return isJavaBuiltInType(psiType.getPresentableText());
    }

    /**
     * 是自定义 JavaBean
     * @param psiType 类型
     * @return {@code true} 是自定义 JavaBean
     */
    public static boolean isCustomType(@NotNull PsiType psiType) {
        return !isJavaBuiltInType(psiType);
    }

    /**
     * Java内置类型
     * @param psiType 类型
     */
    public static boolean isJavaBuiltInType(@NotNull String psiType) {
        return BASE_TYPE.contains(psiType) || BOXED_BASE_TYPE.contains(psiType) || OTHER_TYPE.contains(psiType) || COLLECTION_TYPE.contains(psiType);
    }


    /**
     * 不是集合类型
     * @param psiType 类型
     */
    public static boolean notCollectionType(@NotNull PsiType psiType) {
        return !isCollectionType(psiType);
    }

    /**
     * 是集合类型
     * @param psiType 类型
     */
    public static boolean isCollectionType(@NotNull PsiType psiType) {
        return isCollectionType(psiType.getPresentableText());
    }

    /**
     * 是集合类型
     * @param psiType 类型
     */
    public static boolean isCollectionType(@NotNull String psiType) {
        return COLLECTION_TYPE.contains(psiType);
    }

    @NotNull
    public static List<String> getResultType(PsiType type) {
        if (type == null) {
            return Collections.emptyList();
        }
        if (isJavaBuiltInType(type)) {
            return BaseType.get(type.getPresentableText());
        } else {
            PsiJavaCodeReferenceElement reference = ((PsiClassReferenceType) type).getReference();
            if (StringUtils.isBlank(reference.getReferenceName())) {
                return Collections.emptyList();
            }
            return Collections.singletonList(reference.getQualifiedName());
        }
    }

    private static final Map<String, List<String>> BaseType = Collections.unmodifiableMap(new HashMap<String, List<String>>() {
        private static final long serialVersionUID = -7375291625150519393L;

        {
            this.put("byte", Collections.singletonList("_byte"));
            this.put("long", Collections.singletonList("_long"));
            this.put("short", Collections.singletonList("_short"));
            this.put("int", new ArrayList<String>(2) {
                private static final long serialVersionUID = -4321456431687868856L;

                {
                    this.add("_int");
                    this.add("_integer");
                }
            });
            this.put("double", Collections.singletonList("_double"));
            this.put("float", Collections.singletonList("_float"));
            this.put("boolean", Collections.singletonList("_boolean"));
            this.put("String", Collections.singletonList("string"));
            this.put("Byte", Collections.singletonList("byte"));
            this.put("Long", Collections.singletonList("long"));
            this.put("Short", Collections.singletonList("short"));
            this.put("Integer", new ArrayList<String>(2) {
                private static final long serialVersionUID = -1451201046250936934L;

                {
                    this.add("int");
                    this.add("integer");
                }
            });
            this.put("Double", Collections.singletonList("double"));
            this.put("Float", Collections.singletonList("float"));
            this.put("Boolean", Collections.singletonList("boolean"));
            this.put("Date", Collections.singletonList("date"));
            this.put("Bigdecimal", new ArrayList<String>(2) {
                private static final long serialVersionUID = -304742509443073751L;

                {
                    this.add("decimal");
                    this.add("bigdecimal");
                }
            });
            this.put("Object", Collections.singletonList("object"));
            this.put("Map", Collections.singletonList("map"));
            this.put("Hashmap", Collections.singletonList("hashmap"));
            this.put("List", Collections.singletonList("list"));
            this.put("Arraylist", Collections.singletonList("arraylist"));
            this.put("Collection", Collections.singletonList("collection"));
            this.put("Iterator", Collections.singletonList("iterator"));
        }
    });

}
