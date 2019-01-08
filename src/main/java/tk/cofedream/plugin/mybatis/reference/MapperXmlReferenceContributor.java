package tk.cofedream.plugin.mybatis.reference;

import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.psi.XmlAttributeValueReference;

/**
 * 注册 Xml Attribute 引用
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MapperXmlReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(XmlPatterns.xmlAttributeValue(), new MapperPsiReferenceProvider());
    }

    private static class MapperPsiReferenceProvider extends PsiReferenceProvider {

        @NotNull
        @Override
        public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            return new PsiReference[] {new XmlAttributeValueReference(element)};
        }
    }
}
