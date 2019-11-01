package tk.cofe.plugin.mbsp.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;

/**
 * 引用提供者
 *
 * @author : zhengrf
 * @date : 2019-11-01
 */
public interface MbspReferenceProvider {
    boolean isSupported(IElementType elementType);

    PsiReference exec(PsiElement element);
}
