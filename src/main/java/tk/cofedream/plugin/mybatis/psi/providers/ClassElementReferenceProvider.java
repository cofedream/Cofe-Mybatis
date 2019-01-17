package tk.cofedream.plugin.mybatis.psi.providers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.psi.references.ClassElementReference;

/**
 * @author : zhengrf
 * @date : 2019-01-17
 */
public class ClassElementReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return new PsiReference[] {new ClassElementReference(element)};
    }
}
