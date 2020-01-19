package tk.cofe.plugin.mybatis.psi.reference;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbsp.MbspTypes;

/**
 * @author : zhengrf
 * @date : 2020-01-19
 */
public class MbspReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(MbspTypes.REFERENCE_EXPRESSION), new MbspReferenceProvider());
    }
}
