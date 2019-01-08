package tk.cofedream.plugin.mybatis.psi;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class XmlAttributeValueReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    public XmlAttributeValueReference(@NotNull PsiElement element, TextRange rangeInElement, boolean soft) {
        super(element, rangeInElement, soft);
    }

    public XmlAttributeValueReference(@NotNull PsiElement element, TextRange rangeInElement) {
        super(element, rangeInElement);
    }

    public XmlAttributeValueReference(@NotNull PsiElement element, boolean soft) {
        super(element, soft);
    }

    public XmlAttributeValueReference(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        //Project project = myElement.getProject();
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
    public Object[] getVariants() {
        //List<LookupElement> variants = new ArrayList<LookupElement>();
        LookupElementBuilder typeText = LookupElementBuilder.create(myElement).
                //withIcon(SimpleIcons.FILE).
                        withTypeText(myElement.getContainingFile().getName());
        return new Object[] {typeText};
    }
}
