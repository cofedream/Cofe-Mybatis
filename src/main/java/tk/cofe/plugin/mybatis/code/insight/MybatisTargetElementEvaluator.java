package tk.cofe.plugin.mybatis.code.insight;

import com.intellij.codeInsight.TargetElementEvaluatorEx2;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MybatisTargetElementEvaluator extends TargetElementEvaluatorEx2 {
    @Nullable
    @Override
    public PsiElement getElementByReference(@NotNull PsiReference ref, int flags) {
        return null;
    }
}
