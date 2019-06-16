package tk.cofe.plugin.mybatis.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTypeParameterListOwner;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.service.JavaPsiService;

/**
 * @author : zhengrf
 * @date : 2019-01-03
 */
public class MapperDefinitionSearch extends QueryExecutorBase<XmlElement, PsiElement> {
    public MapperDefinitionSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull PsiElement queryParameters, @NotNull Processor<? super XmlElement> consumer) {
        if (!(queryParameters instanceof PsiTypeParameterListOwner)) {
            return;
        }
        JavaPsiService.getInstance(queryParameters.getProject()).process(queryParameters, (Processor<DomElement>) classElement -> consumer.process(classElement.getXmlElement()));
    }
}
