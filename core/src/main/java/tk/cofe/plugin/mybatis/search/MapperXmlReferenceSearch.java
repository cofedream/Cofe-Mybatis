package tk.cofe.plugin.mybatis.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.tag.ResultMap;
import tk.cofe.plugin.mybatis.psi.ResultMapReference;
import tk.cofe.plugin.mybatis.psi.reference.XmlAttributeValueReference;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author : zhengrf
 * @date : 2019-11-22
 */
public class MapperXmlReferenceSearch {
    /**
     * @author : zhengrf
     * @date : 2019-01-23
     */
    public static class ResultMapId extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

        private static final ResultMapReference RESULT_MAP_REFERENCE = new ResultMapReference((xmlAttributeValue, element) -> new XmlAttributeValueReference(xmlAttributeValue));

        public ResultMapId() {
            super(true);
        }

        @Override
        public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
            PsiElement targetElement = queryParameters.getElementToSearch();
            if (!(targetElement instanceof XmlAttributeValue)) {
                return;
            }
            if (!DomUtils.isTargetDomElement(targetElement, ResultMap.class)) {
                return;
            }
            XmlAttributeValue element = (XmlAttributeValue) targetElement;
            String rawText = element.getValue();
            Optional<Mapper> mapperFile = MybatisUtils.getMapper(element);
            // resultMap extends
            mapperFile.map(RESULT_MAP_REFERENCE.getResultMapFunction(element, rawText)).ifPresent(process(consumer));
            // select resultMap
            mapperFile.map(RESULT_MAP_REFERENCE.getSelectFunction(element, rawText)).ifPresent(process(consumer));
            // resultMap inside resultMap Attributes
            mapperFile.map(RESULT_MAP_REFERENCE.getResultMapAttributeFunction(element, rawText)).ifPresent(process(consumer));
        }

        private Consumer<PsiReference[]> process(@NotNull final Processor<? super PsiReference> consumer) {
            return psiReferences -> {
                for (PsiReference psiReference : psiReferences) {
                    consumer.process(psiReference);
                }
            };
        }

    }
}
