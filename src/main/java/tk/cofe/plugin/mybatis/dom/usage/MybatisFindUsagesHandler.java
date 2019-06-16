package tk.cofe.plugin.mybatis.dom.usage;

import com.intellij.find.findUsages.AbstractFindUsagesDialog;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author : zhengrf
 * @date : 2019-01-04
 */
public class MybatisFindUsagesHandler extends FindUsagesHandler {

    private final MybatisFindUsagesHandlerFactory factory;

    protected MybatisFindUsagesHandler(@NotNull PsiElement psiElement, @NotNull MybatisFindUsagesHandlerFactory factory) {
        super(psiElement);
        this.factory = factory;
    }

    @NotNull
    @Override
    public AbstractFindUsagesDialog getFindUsagesDialog(boolean isSingleFile, boolean toShowInNewTab, boolean mustOpenInNewTab) {
        return super.getFindUsagesDialog(isSingleFile, toShowInNewTab, mustOpenInNewTab);
    }

    @NotNull
    @Override
    public PsiElement[] getPrimaryElements() {
        return super.getPrimaryElements();
    }

    @NotNull
    @Override
    public PsiElement[] getSecondaryElements() {
        return super.getSecondaryElements();
    }

    @NotNull
    @Override
    public FindUsagesOptions getFindUsagesOptions(@Nullable DataContext dataContext) {
        return super.getFindUsagesOptions(dataContext);
    }

    @Nullable
    @Override
    protected Collection<String> getStringsToSearch(@NotNull PsiElement element) {
        return super.getStringsToSearch(element);
    }

    @Override
    public boolean processElementUsages(@NotNull PsiElement element, @NotNull Processor<UsageInfo> processor, @NotNull FindUsagesOptions options) {
        return super.processElementUsages(element, processor, options);
    }

    @Override
    protected boolean isSearchForTextOccurrencesAvailable(@NotNull PsiElement psiElement, boolean isSingleFile) {
        return super.isSearchForTextOccurrencesAvailable(psiElement, isSingleFile);
    }

    @NotNull
    @Override
    public Collection<PsiReference> findReferencesToHighlight(@NotNull PsiElement target, @NotNull SearchScope searchScope) {
        return super.findReferencesToHighlight(target, searchScope);
    }

}
