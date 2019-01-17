package tk.cofedream.plugin.mybatis.referencessearch;

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
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ClassElement;
import tk.cofedream.plugin.mybatis.dom.psi.MapperXmlReferenceContributor;
import tk.cofedream.plugin.mybatis.dom.psi.references.PsiIdentifierReference;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;
import tk.cofedream.plugin.mybatis.service.MapperService;
import tk.cofedream.plugin.mybatis.utils.MapperUtils;

import java.util.Optional;

/**
 * 从 XML标签定位到接口方法
 * @author : zhengrf
 * @date : 2019-01-05
 * @deprecated 使用 {@link MapperXmlReferenceContributor}
 * @see MapperXmlReferenceContributor
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
        if (!MapperService.isMapperXmlFile(elementToSearch.getContainingFile())) {
            return;
        }
        XmlTag tag = PsiTreeUtil.getParentOfType(elementToSearch.getContext(), XmlTag.class, true);
        if (MapperUtils.isBaseStatementElement(tag)) {
            ClassElement classElement = (ClassElement) DomUtil.getDomElement(tag);
            Optional.ofNullable(classElement).ifPresent(element -> element.getNamespaceValue().ifPresent(qualifiedName ->
                    JavaPsiService.getInstance(project).getPsiClass(qualifiedName).ifPresent(psiClass -> {
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
