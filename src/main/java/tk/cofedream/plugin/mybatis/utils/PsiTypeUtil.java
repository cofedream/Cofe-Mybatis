package tk.cofedream.plugin.mybatis.utils;

import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author : zhengrf
 * @date : 2019-01-08
 */
public class PsiTypeUtil {

    private static final Collection<String> BASE_TYPE = Arrays.asList("byte", "short", "int", "long", "float", "double", "boolean", "char");
    private static final Collection<String> BOXED_BASE_TYPE = Arrays.asList("Byte", "Short", "Integer", "Long", "Float", "Double", "Boolean", "Character");
    private static final Collection<String> OTHER_TYPE = Arrays.asList("String", "Date", "Bigdecimal", "Object", "Iterator");
    private static final Collection<String> COLLECTION_TYPE = Arrays.asList("Map", "Hashmap", "TreeMap",
            "List", "ArrayList", "LinkedList",
            "Set", "HashSet", "TreeSet"
            , "Collection");

    public static boolean notCustomType(@NotNull PsiType psiType) {
        // 获取显示的文本内容 Boolean/List<String>
        return notCustomType(psiType.getPresentableText());
    }

    /**
     * 是自定义 JavaBean
     * @param psiType 类型
     * @return {@code true} 是自定义 JavaBean
     */
    public static boolean isCustomType(@NotNull PsiType psiType) {
        return !notCustomType(psiType);
    }

    public static boolean notCustomType(@NotNull String psiType) {
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

}
