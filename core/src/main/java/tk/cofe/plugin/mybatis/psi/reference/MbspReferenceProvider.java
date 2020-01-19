package tk.cofe.plugin.mybatis.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbsp.psi.MbspReferenceExpression;
import tk.cofe.plugin.mbsp.psi.impl.MbspPsiUtil;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.CompletionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.LinkedList;

/**
 * @author : zhengrf
 * @date : 2020-01-19
 */
public class MbspReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull final ProcessingContext context) {
        final PsiElement originElement = MbspPsiUtil.getOriginElement(element);
        if (originElement == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        LinkedList<String> res = new LinkedList<>();
        res.push(element.getText());
        PsiElement prevSibling = element;
        while ((prevSibling = prevSibling.getPrevSibling()) != null) {
            if (prevSibling instanceof MbspReferenceExpression) {
                res.push(prevSibling.getText());
            }
        }
        PsiElement resolveTo = DomUtils.getDomElement(originElement, ClassElement.class)
                .flatMap(ClassElement::getIdMethod)
                .map(psiMethod -> CompletionUtils.getPrefixElement(res.toArray(new String[0]), psiMethod.getParameterList().getParameters()))
                .orElse(null);
        if (resolveTo == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        return new PsiReference[]{new PsiReferenceBase.Immediate<>(element, new TextRange(0, element.getTextLength()), resolveTo)};
    }
}
