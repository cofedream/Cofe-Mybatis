package tk.cofedream.plugin.mybatis.psi;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class PsiIdentifierReference extends PsiReferenceBase<PsiIdentifier> implements PsiPolyVariantReference {
    public PsiIdentifierReference(@NotNull PsiIdentifier element, TextRange rangeInElement, boolean soft) {
        super(element, rangeInElement, soft);
    }

    public PsiIdentifierReference(@NotNull PsiIdentifier element, TextRange rangeInElement) {
        super(element, rangeInElement);
    }

    public PsiIdentifierReference(@NotNull PsiIdentifier element, boolean soft) {
        super(element, soft);
    }

    public PsiIdentifierReference(@NotNull PsiIdentifier element) {
        super(element);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        PsiElementResolveResult resolveResult = new PsiElementResolveResult(myElement);
        return new ResolveResult[] {resolveResult};
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        //TextRange textRange = myElement.getTextRange();
        //TextRange textRangeInParent = myElement.getTextRangeInParent();
        //return myElement.getParent().getTextRange();
        //return super.getRangeInElement();
        TextRange textRange = new TextRange(1, myElement.getTextLength());
        return textRange;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        LookupElementBuilder typeText = LookupElementBuilder.create(myElement).
                withTypeText(myElement.getContainingFile().getName());
        return new Object[] {typeText};
    }
}
