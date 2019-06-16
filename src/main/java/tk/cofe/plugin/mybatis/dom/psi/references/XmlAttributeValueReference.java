package tk.cofe.plugin.mybatis.dom.psi.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;

/**
 * @author : zhengrf
 * @date : 2019-01-20
 */
public class XmlAttributeValueReference extends PsiReferenceBase.Poly<PsiElement> {

    public XmlAttributeValueReference(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return new ResolveResult[] {new PsiElementResolveResult(myElement)};
    }

}
