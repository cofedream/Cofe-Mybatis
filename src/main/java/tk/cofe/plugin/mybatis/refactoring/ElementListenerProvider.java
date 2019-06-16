package tk.cofe.plugin.mybatis.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 重构监听器
 * @author : zhengrf
 * @date : 2019-01-06
 */
public class ElementListenerProvider implements RefactoringElementListenerProvider {
    @Nullable
    @Override
    public RefactoringElementListener getListener(PsiElement element) {
        return new RefactoringElementListener() {
            @Override
            public void elementMoved(@NotNull PsiElement newElement) {

            }

            @Override
            public void elementRenamed(@NotNull PsiElement newElement) {

            }
        };
    }
}
