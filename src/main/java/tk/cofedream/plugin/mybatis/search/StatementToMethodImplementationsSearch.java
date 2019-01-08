package tk.cofedream.plugin.mybatis.search;

import com.intellij.openapi.project.DumbService;
import com.intellij.pom.PomTarget;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomTarget;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.dom.mapper.model.ClassElement;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;

/**
 * Mapper 跳转 XML 标签定义 CTRL+ALT+B
 * 会出现两个标签
 * @author : zhengrf
 * @date : 2019-01-03
 */
public class StatementToMethodImplementationsSearch implements QueryExecutor<PsiMethod, DefinitionsScopedSearch.SearchParameters> {

    @Override
    public final boolean execute(@NotNull final DefinitionsScopedSearch.SearchParameters queryParameters, @NotNull final Processor<? super PsiMethod> consumer) {
        PsiElement element = queryParameters.getElement();
        if (!(element instanceof PomTargetPsiElement)) {
            return true;
        }
        PomTarget pomTarget = ((PomTargetPsiElement) element).getTarget();
        if (!(pomTarget instanceof DomTarget)) {
            return true;
        }
        DomElement domElement = ((DomTarget) pomTarget).getDomElement();
        if (!(domElement instanceof ClassElement)) {
            return true;
        }
        DumbService.getInstance(element.getProject()).runReadActionInSmartMode(() ->
                JavaPsiService.getInstance(element.getProject()).findMethod(((ClassElement) domElement)).ifPresent(psiMethods -> {
                    for (PsiMethod psiMethod : psiMethods) {
                        if (((ClassElement) domElement).getIdValue().map(id -> id.equals(psiMethod.getName())).orElse(false)) {
                            consumer.process(psiMethod);
                        }
                    }
                }));
        return false;
    }
}
