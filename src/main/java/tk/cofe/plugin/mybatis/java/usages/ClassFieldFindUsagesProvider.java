package tk.cofe.plugin.mybatis.java.usages;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.java.JavaFindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author : zhengrf
 * @date : 2019-06-16
 */
public class ClassFieldFindUsagesProvider extends JavaFindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return null;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        System.out.println(psiElement.getText());
        return false;
    }

}
