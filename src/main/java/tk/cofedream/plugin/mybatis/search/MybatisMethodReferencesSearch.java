package tk.cofedream.plugin.mybatis.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.util.Processor;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofedream.plugin.mybatis.dom.mapper.model.ClassElement;
import tk.cofedream.plugin.mybatis.dom.mapper.model.Mapper;
import tk.cofedream.plugin.mybatis.reference.MapperXmlReferenceContributor;
import tk.cofedream.plugin.mybatis.service.MapperService;

import java.util.Collection;
import java.util.Optional;

/**
 * 接口方法定位到XML标签
 * @author : zhengrf
 * @date : 2019-01-05
 * @see MapperXmlReferenceContributor
 */
public class MybatisMethodReferencesSearch extends QueryExecutorBase<PsiReference, MethodReferencesSearch.SearchParameters> {
    public MybatisMethodReferencesSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull MethodReferencesSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiMethod method = queryParameters.getMethod();
        if (method.getContainingClass() != null) {
            Collection<Mapper> mappers = MapperService.getInstance(queryParameters.getProject()).findMapperXmls(method.getContainingClass());
            mappers.forEach(mapperXml -> mapperXml.getClassElements().forEach(element -> {
                if (element.getIdValue().map(id -> id.equals(method.getName())).orElse(false)) {
                    Optional.of(element)
                            .map(ClassElement::getId)
                            .map(GenericAttributeValue::getXmlAttributeValue)
                            .map(PsiElement::getReference)
                            .ifPresent(consumer::process);
                }
            }));
        }
    }
}
