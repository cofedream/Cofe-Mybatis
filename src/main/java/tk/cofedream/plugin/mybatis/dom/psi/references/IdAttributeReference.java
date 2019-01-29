package tk.cofedream.plugin.mybatis.dom.psi.references;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;
import tk.cofedream.plugin.mybatis.service.MapperService;
import tk.cofedream.plugin.mybatis.utils.EmptyUtil;

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
            JavaPsiService.getInstance(project).findMethod((ClassElement) DomUtil.getDomElement(tag)).ifPresent(psiMethod -> result.add(new PsiElementResolveResult(psiMethod)));
        }
        if (result.isEmpty()) {
            result.add(new PsiElementResolveResult(myElement));
        }
        return result.toArray(EmptyUtil.Array.RESOLVE_RESULT);
    }

}
