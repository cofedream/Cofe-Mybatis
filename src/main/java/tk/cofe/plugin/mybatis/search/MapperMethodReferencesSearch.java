package tk.cofe.plugin.mybatis.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Mapper;
import tk.cofe.plugin.mybatis.dom.psi.MapperReferenceContributor;
import tk.cofe.plugin.mybatis.dom.psi.references.IdAttributeReference;
import tk.cofe.plugin.mybatis.service.MapperService;

import java.util.Collection;
import java.util.Optional;

/**
 * 接口方法定位到XML标签 Ctrl+B
 * @author : zhengrf
 * @date : 2019-01-05
 * @see MapperReferenceContributor
 */
public class MapperMethodReferencesSearch extends QueryExecutorBase<PsiReference, com.intellij.psi.search.searches.MethodReferencesSearch.SearchParameters> {
    public MapperMethodReferencesSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull com.intellij.psi.search.searches.MethodReferencesSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiMethod method = queryParameters.getMethod();
        if (method.getContainingClass() != null) {
            Collection<Mapper> mappers = MapperService.getInstance(queryParameters.getProject()).findMapperXmls(method.getContainingClass());
            mappers.forEach(mapperXml -> mapperXml.getClassElements().forEach(element -> {
                if (element.getIdValue().map(id -> id.equals(method.getName())).orElse(false)) {
                    Optional.of(element)
                            .map(ClassElement::getId)
                            .map(attributeValue -> {
                                XmlAttributeValue xmlAttributeValue = attributeValue.getXmlAttributeValue();
                                return xmlAttributeValue == null ? null : new IdAttributeReference(xmlAttributeValue);
                            })
                            .ifPresent(consumer::process);
                }
            }));
        }
    }
}
