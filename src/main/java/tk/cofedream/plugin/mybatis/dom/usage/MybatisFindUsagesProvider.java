package tk.cofedream.plugin.mybatis.dom.usage;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.ClassElement;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;
import tk.cofedream.plugin.mybatis.service.MapperService;
import tk.cofedream.plugin.mybatis.utils.MapperUtils;

import java.util.Optional;

/**
 * @author : zhengrf
 * @date : 2019-01-04
 */
public class MybatisFindUsagesProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return null;
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        if (MapperService.isMapperXmlFile(psiElement.getContainingFile())) {
            PsiElement context = psiElement.getContext();
            XmlTag tag = PsiTreeUtil.getParentOfType(context, XmlTag.class, true);
            if (MapperUtils.isBaseStatementElement(tag)) {
                ClassElement classElement = (ClassElement) DomUtil.getDomElement(tag);
                Optional<PsiMethod[]> method = JavaPsiService.getInstance(psiElement.getProject()).findMethod(classElement);
                return method.isPresent();
            }
        }
        return false;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        return "null";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        return "null";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return "null";
    }
}
