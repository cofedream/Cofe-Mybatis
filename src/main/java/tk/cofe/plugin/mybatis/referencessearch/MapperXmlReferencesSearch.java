package tk.cofe.plugin.mybatis.referencessearch;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.psi.MapperReferenceContributor;
import tk.cofe.plugin.mybatis.dom.psi.references.PsiIdentifierReference;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;

import java.util.Optional;

/**
 * 从 XML标签定位到接口方法<br/>
 * 仅可引用，如果要重命名则需另外编写
 * @author : zhengrf
 * @date : 2019-01-05
 * @see MapperReferenceContributor
 * @deprecated 使用 {@link MapperReferenceContributor}
 */
@Deprecated
public class MapperXmlReferencesSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    public MapperXmlReferencesSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement elementToSearch = queryParameters.getElementToSearch();
        Project project = queryParameters.getProject();
        if (!elementToSearch.getLanguage().is(XMLLanguage.INSTANCE)) {
            return;
        }
        if (!PsiMybatisUtils.isMapperXmlFile(elementToSearch.getContainingFile())) {
            return;
        }
        XmlTag tag = PsiTreeUtil.getParentOfType(elementToSearch.getContext(), XmlTag.class, true);
        if (PsiMybatisUtils.isBaseStatementElement(tag)) {
            Optional.ofNullable((ClassElement) DomUtils.getDomElement(tag)).ifPresent(element -> element.getNamespaceValue().ifPresent(qualifiedName ->
                    JavaPsiService.getInstance(project).findPsiClass(qualifiedName).ifPresent(psiClass -> {
                        PsiMethod[] methods = psiClass.getMethods();
                        for (PsiMethod method : methods) {
                            PsiIdentifier nameIdentifier = method.getNameIdentifier();
                            if (element.getIdValue().map(id -> id.equals(method.getName())).orElse(false) && nameIdentifier != null) {
                                consumer.process(new PsiIdentifierReference(nameIdentifier));
                            }
                        }
                    })));
        }
    }
}
