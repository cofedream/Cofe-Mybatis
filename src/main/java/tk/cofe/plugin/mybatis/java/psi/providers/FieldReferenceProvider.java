package tk.cofe.plugin.mybatis.java.psi.providers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.ResolveResult;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * 字段引用XML
 * @author : zhengrf
 * @date : 2019-01-22
 */
public class FieldReferenceProvider extends PsiReferenceProvider {

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return new PsiReference[] {new FieldReference(element)};
    }

    private static class FieldReference extends PsiReferenceBase.Poly<PsiElement> {

        public FieldReference(@NotNull PsiElement element) {
            super(element);
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean incompleteCode) {
            return new ResolveResult[0];
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return new Object[0];
        }
    }
}
