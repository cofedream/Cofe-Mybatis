package tk.cofedream.plugin.mybatis.utils;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author : zhengrf
 * @date : 2019-01-01
 */
public class JavaPsiUtils {

    /**
     * 判断 PsiElement 是否为接口
     * @param psiElement 元素
     * @return 如果是接口则返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isInterface(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiClass && ((PsiClass) psiElement).isInterface();
    }

    /**
     * 判断 PsiElement 是否为类方法
     * @param psiElement 元素
     * @return 如果是接口则返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isInterfaceMethod(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiMethod && isElementWithinInterface(psiElement);
    }

    private static boolean isElementWithinInterface(@Nullable PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass parentOfType = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return parentOfType != null && parentOfType.isInterface();
    }


}
