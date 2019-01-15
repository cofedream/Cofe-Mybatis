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
    private static final Collection<String> OTHER_TYPE = Arrays.asList("String", "Date", "Bigdecimal", "Object", "Map", "Hashmap", "List", "ArrayList", "Collection", "Iterator");

    public static boolean notCustomType(@NotNull PsiType psiType) {
        // 获取显示的文本内容 Boolean/List<String>
        return notCustomType(psiType.getPresentableText());
    }

    public static boolean notCustomType(@NotNull String psiType) {
        return BASE_TYPE.contains(psiType) || BOXED_BASE_TYPE.contains(psiType) || OTHER_TYPE.contains(psiType);
    }

}
