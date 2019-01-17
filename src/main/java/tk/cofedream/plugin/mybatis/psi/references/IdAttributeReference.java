package tk.cofedream.plugin.mybatis.psi.references;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;
import tk.cofedream.plugin.mybatis.utils.MapperUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Xml Attribute 引用
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class IdAttributeReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    public IdAttributeReference(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> result = new ArrayList<>();
        XmlTag tag = PsiTreeUtil.getParentOfType(myElement, XmlTag.class);
        if (MapperUtils.isBaseStatementElement(tag)) {
            Project project = myElement.getProject();
            JavaPsiService.getInstance(project).findMethod((ClassElement) DomUtil.getDomElement(tag)).ifPresent(psiMethods -> {
                for (PsiMethod psiMethod : psiMethods) {
                    result.add(new PsiElementResolveResult(psiMethod));
                }
            });
        }
        if (result.isEmpty()) {
            result.add(new PsiElementResolveResult(myElement));
        }
        return result.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    //@NotNull
    //@Override
    //public Object[] getVariants() {
    //    ////List<LookupElement> variants = new ArrayList<LookupElement>();
    //    //LookupElementBuilder typeText = LookupElementBuilder.create(myElement).
    //    //        //withIcon(SimpleIcons.FILE).
    //    //                withTypeText(myElement.getContainingFile().getName());
    //    //return new Object[] {typeText};
    //    return new Object[0];
    //}
}
