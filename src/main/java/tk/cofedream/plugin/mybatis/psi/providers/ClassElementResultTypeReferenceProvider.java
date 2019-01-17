package tk.cofedream.plugin.mybatis.psi.providers;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.ResolveResult;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author : zhengrf
 * @date : 2019-01-17
 */
public class ClassElementResultTypeReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return new PsiReference[] {new ResultTypeAttributeReference(element)};
    }
    /**
     * Xml Attribute 引用
     * @author : zhengrf
     * @date : 2019-01-05
     */
    private static class ResultTypeAttributeReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
        ResultTypeAttributeReference(@NotNull PsiElement element) {
            super(element);
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean incompleteCode) {
            return new ResolveResult[] {new PsiElementResolveResult(myElement)};
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            ResolveResult[] resolveResults = multiResolve(false);
            return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            ////List<LookupElement> variants = new ArrayList<LookupElement>();
            LookupElementBuilder typeText = LookupElementBuilder.create("aaa").
                    //withIcon(SimpleIcons.FILE).
                            withTypeText(myElement.getContainingFile().getName());
            return new Object[] {typeText};
        }
    }

}
