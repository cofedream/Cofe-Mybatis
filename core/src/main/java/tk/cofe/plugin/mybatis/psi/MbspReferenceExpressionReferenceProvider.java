package tk.cofe.plugin.mybatis.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.tree.IElementType;
import tk.cofe.plugin.mbsp.MbspTypes;
import tk.cofe.plugin.mbsp.psi.MbspReferenceExpression;
import tk.cofe.plugin.mbsp.psi.MbspReferenceProvider;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.CompletionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.LinkedList;

/**
 * @author : zhengrf
 * @date : 2019-11-01
 */
public class MbspReferenceExpressionReferenceProvider implements MbspReferenceProvider {
    @Override
    public boolean isSupported(final IElementType elementType) {
        return MbspTypes.REFERENCE_EXPRESSION.equals(elementType);
    }

    @Override
    public PsiReference exec(final PsiElement element, final PsiElement originElement) {
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
                .map(psiMethod -> CompletionUtils.getPrefixElement(res.toArray(new String[0]), ((PsiParameter[]) psiMethod.getParameters())))
                .orElse(null);
        if (resolveTo == null) {
            return null;
        }
        return new PsiReferenceBase.Immediate<>(element, new TextRange(0, element.getTextLength()), resolveTo);
    }

}
