package tk.cofedream.plugin.mybatis.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTypeParameterListOwner;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;

/**
 * Mapper 跳转 XML 标签定义 CTRL+ALT+B
 * @author : zhengrf
 * @date : 2019-01-03
 */
public class MethodToStatementImplementationsSearch extends QueryExecutorBase<XmlElement, DefinitionsScopedSearch.SearchParameters> {
    public MethodToStatementImplementationsSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull DefinitionsScopedSearch.SearchParameters queryParameters, @NotNull Processor<? super XmlElement> consumer) {
        PsiElement element = queryParameters.getElement();
        if (!(element instanceof PsiTypeParameterListOwner)) {
            return;
        }
        Project project = queryParameters.getProject();
        if (project == null) {
            return;
        }
        JavaPsiService.getInstance(project).process(element, (Processor<DomElement>) classElement -> consumer.process(classElement.getXmlElement()));
    }
}
