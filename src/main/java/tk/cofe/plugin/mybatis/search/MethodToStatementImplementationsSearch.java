package tk.cofe.plugin.mybatis.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.dom.mapper.model.tag.ClassElement;

import java.util.Objects;

/**
 * Mapper 跳转 XML 标签定义 CTRL+ALT+B
 * @author : zhengrf
 * @date : 2019-01-03
 */
public class MethodToStatementImplementationsSearch extends QueryExecutorBase<XmlAttributeValue, DefinitionsScopedSearch.SearchParameters> {
    public MethodToStatementImplementationsSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull DefinitionsScopedSearch.SearchParameters queryParameters, @NotNull Processor<? super XmlAttributeValue> consumer) {
        PsiElement element = queryParameters.getElement();
        if (!(element instanceof PsiMethod)) {
            return;
        }
        Project project = queryParameters.getProject();
        if (project == null) {
            return;
        }
        JavaPsiService.getInstance(project).process(element, (Processor<ClassElement>) classElement ->
                consumer.process(Objects.requireNonNull(classElement.getId()).getXmlAttributeValue()));
    }
}