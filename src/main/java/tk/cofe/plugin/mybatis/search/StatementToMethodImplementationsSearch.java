package tk.cofe.plugin.mybatis.search;

import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.service.MapperService;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * XML 跳转 Mapper 标签定义 CTRL+ALT+B
 * 会出现两个标签
 * @author : zhengrf
 * @date : 2019-01-03
 */
public class StatementToMethodImplementationsSearch implements QueryExecutor<PsiMethod, DefinitionsScopedSearch.SearchParameters> {

    @Override
    public final boolean execute(@NotNull final DefinitionsScopedSearch.SearchParameters queryParameters, @NotNull final Processor<? super PsiMethod> consumer) {
        PsiElement element = queryParameters.getElement();
        AtomicBoolean result = new AtomicBoolean(false);
        DumbService.getInstance(element.getProject()).runReadActionInSmartMode(() -> {
            XmlTag xmlTag = PsiTreeUtil.getParentOfType(element, XmlTag.class);
            if (!MapperService.isBaseStatementElement(xmlTag)) {
                return;
            }
            ClassElement classElement = (ClassElement) DomUtils.getDomElement(xmlTag);
            if (classElement == null) {
                return;
            }
            JavaPsiService.getInstance(element.getProject()).findPsiMethods(classElement).ifPresent(psiMethods -> {
                for (PsiMethod psiMethod : psiMethods) {
                    if ((classElement).getIdValue().map(id -> id.equals(psiMethod.getName())).orElse(false)) {
                        consumer.process(psiMethod);
                    }
                }
            });
            result.set(true);
        });
        return result.get();
    }

}
