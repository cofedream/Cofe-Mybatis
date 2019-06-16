package tk.cofe.plugin.mybatis.dom.psi.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.refactoring.rename.RenameUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.util.CollectionUtils;
import tk.cofe.plugin.mybatis.dom.psi.MapperReferenceContributor;

import java.util.Collections;

/**
 * @author : zhengrf
 * @date : 2019-01-05
 * @see MapperReferenceContributor
 */
public class PsiIdentifierReference extends PsiReferenceBase.Poly<PsiIdentifier> {

    public PsiIdentifierReference(@NotNull PsiIdentifier element) {
        super(element);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return new ResolveResult[] {new PsiElementResolveResult(myElement)};
    }

    @NotNull
    @Override
    public TextRange getRangeInElement() {
        return new TextRange(1, myElement.getTextLength());
    }

    /**
     * 处理元素重命名
     */
    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        PsiElement element = myElement.getParent();
        if (element instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) element;
            UsageInfo[] methodUsage = RenameUtil.findUsages(psiMethod, newElementName, true, true, Collections.singletonMap(psiMethod, psiMethod.getName()));
            if (!CollectionUtils.isEmpty(methodUsage)) {
                RenamePsiElementProcessor processor = RenamePsiElementProcessor.forElement(psiMethod);
                processor.renameElement(psiMethod, newElementName, methodUsage, null);
            }
        }
        return element;
    }
}
