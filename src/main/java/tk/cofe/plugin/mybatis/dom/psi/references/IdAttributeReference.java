package tk.cofe.plugin.mybatis.dom.psi.references;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.constants.Empty;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.service.MapperService;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Xml Attribute 引用
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class IdAttributeReference extends PsiReferenceBase.Poly<PsiElement> {
    public IdAttributeReference(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> result = new ArrayList<>();
        XmlTag tag = PsiTreeUtil.getParentOfType(myElement, XmlTag.class);
        if (MapperService.isBaseStatementElement(tag)) {
            Project project = myElement.getProject();
            JavaPsiService.getInstance(project).findPsiMethod((ClassElement) DomUtils.getDomElement(tag)).ifPresent(psiMethod -> result.add(new PsiElementResolveResult(psiMethod)));
        }
        if (result.isEmpty()) {
            result.add(new PsiElementResolveResult(myElement));
        }
        return result.toArray(Empty.Array.RESOLVE_RESULT);
    }

}
