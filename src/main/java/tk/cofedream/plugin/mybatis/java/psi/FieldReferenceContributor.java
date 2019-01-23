package tk.cofedream.plugin.mybatis.java.psi;

import com.intellij.patterns.PsiFieldPattern;
import com.intellij.patterns.PsiJavaPatterns;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.java.psi.providers.FieldReferenceProvider;

/**
 * 字段引用
 * @author : zhengrf
 * @date : 2019-01-22
 */
public class FieldReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        PsiFieldPattern pattern = PsiJavaPatterns.psiField();
        registrar.registerReferenceProvider(pattern, new FieldReferenceProvider());
    }

}
