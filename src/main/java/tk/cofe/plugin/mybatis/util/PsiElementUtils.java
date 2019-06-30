package tk.cofe.plugin.mybatis.util;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Element 工具类
 * @author : zhengrf
 * @date : 2019-06-30
 */
public class PsiElementUtils {
    /**
     * 从当前编辑框获取当前光标所在元素
     * @param editor 编辑框
     * @return 当前光标所在元素
     */
    @Nullable
    public static PsiElement getElement(@NotNull Editor editor) {
        return PsiUtilBase.getElementAtCaret(editor);
    }

    /**
     * 从当前编辑框获取当前光标所在元素
     * @param editor 编辑框
     * @return 当前光标所在元素
     */
    @Nullable
    public static PsiElement getElement(@NotNull Editor editor, @NotNull PsiFile psiFile) {
        return psiFile.findElementAt(editor.getCaretModel().getOffset());
    }

    /**
     * 从当前编辑框获取当前光标所在目标元素
     * @param editor 编辑框
     * @param target 目标元素
     * @return 当前光标所在元素
     */
    @Nullable
    public static <T extends PsiElement> T getElement(@NotNull Editor editor, @NotNull Class<T> target) {
        PsiElement element = PsiUtilBase.getElementAtCaret(editor);
        if (element == null) {
            return null;
        }
        return PsiTreeUtil.getParentOfType(element, target);
    }
}
