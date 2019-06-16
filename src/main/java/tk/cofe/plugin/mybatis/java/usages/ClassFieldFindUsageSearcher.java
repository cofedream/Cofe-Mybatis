package tk.cofe.plugin.mybatis.java.usages;

import com.intellij.find.findUsages.CustomUsageSearcher;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.psi.PsiElement;
import com.intellij.usages.Usage;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

/**
 * @author : zhengrf
 * @date : 2019-06-16
 */
public class ClassFieldFindUsageSearcher extends CustomUsageSearcher {
    @Override
    public void processElementUsages(@NotNull PsiElement element, @NotNull Processor<Usage> processor, @NotNull FindUsagesOptions options) {
        System.out.println(element.getText());
    }
}