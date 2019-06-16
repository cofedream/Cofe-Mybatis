package tk.cofe.plugin.mybatis.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.service.MapperService;
import tk.cofe.plugin.mybatis.dom.mapper.model.attirubte.PropertyAttribute;
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author : zhengrf
 * @date : 2019-01-23
 */
public class FieldReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

    public FieldReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement fieldElement = queryParameters.getElementToSearch();
        if (!(fieldElement instanceof PsiField)) {
            return;
        }
        PsiField psiField = (PsiField) fieldElement;
        PsiClass psiClass = psiField.getContainingClass();
        if (psiClass == null) {
            return;
        }
        String classQualifiedName = psiClass.getQualifiedName();
        if (StringUtils.isBlank(classQualifiedName)) {
            return;
        }
        JavaPsiService javaPsiService = JavaPsiService.getInstance(queryParameters.getProject());
        MapperService.getInstance(queryParameters.getProject()).findAllMappers().forEach(mapper -> mapper.getResultMaps().forEach(resultMap -> {
            resultMap.getTypeValue().ifPresent(type -> javaPsiService.getPsiClass(type).ifPresent(typeClass -> {
                if (isTarget(psiClass, classQualifiedName)) {
                    process(resultMap.getPropertyAttributes(), psiField, consumer);
                }
            }));
        }));
    }

    private void process(@NotNull List<? extends PropertyAttribute> attributes, @NotNull PsiField psiField, @NotNull Processor<? super PsiReference> consumer) {
        attributes.forEach(id -> {
            XmlAttributeValue xmlAttributeValue = id.getProperty().getXmlAttributeValue();
            if (xmlAttributeValue != null && Objects.equals(psiField.getName(), xmlAttributeValue.getValue())) {
                consumer.process(new PsiReferenceBase.Immediate<>(xmlAttributeValue, xmlAttributeValue));
            }
        });
    }

    private boolean isTarget(@Nullable PsiClass targetClass, @NotNull String currentClassQualifiedName) {
        if (targetClass == null) {
            return false;
        }
        if (currentClassQualifiedName.equals(targetClass.getQualifiedName())) {
            return true;
        }
        if ("java.lang.Object".equals(targetClass.getQualifiedName())) {
            return false;
        }
        return isTarget(targetClass.getSuperClass(), currentClassQualifiedName);
    }
}
