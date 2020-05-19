package tk.cofe.plugin.mybatis.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbsp.psi.impl.MbspPsiUtil;
import tk.cofe.plugin.mybatis.dom.model.attirubte.NameAttribute;
import tk.cofe.plugin.mybatis.dom.model.include.BindInclude;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.CompletionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2020-01-19
 */
public class MbspReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull final ProcessingContext context) {
        if (element.getTextLength() <= 0) {
            return PsiReference.EMPTY_ARRAY;
        }
        final PsiElement originElement = MbspPsiUtil.getOriginElement(element);
        if (originElement == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        String text = element.getText();
        final String[] splitText = text.split("\\.");
        // bind 标签
        final List<PsiReference> binds = DomUtils.getParents(originElement, XmlTag.class, BindInclude.class).stream()
                .flatMap(info -> info.getBinds().stream())
                .map(NameAttribute::getName)
                .filter(bind -> Objects.equals(text, DomUtils.getAttributeVlaue(bind).orElse(null)))
                .map(GenericAttributeValue::getXmlAttributeValue)
                .filter(Objects::nonNull)
                .map(bind -> new PsiReferenceBase.Immediate<>(element, new TextRange(0, bind.getTextLength()), bind))
                .collect(Collectors.toList());
        // 方法参数
        final List<PsiReference> methodParam = DomUtils.getDomElement(originElement, ClassElement.class)
                .flatMap(ClassElement::getIdMethod)
                .map(psiMethod -> CompletionUtils.getPrefixElement(splitText, psiMethod.getParameterList().getParameters()))
                .map(resolveTo -> Collections.<PsiReference>singletonList(new PsiReferenceBase.Immediate<>(element, new TextRange(0, element.getTextLength()), resolveTo)))
                .orElse(Collections.emptyList());
        List<PsiReference> res = new ArrayList<>(binds.size() + methodParam.size());
        res.addAll(binds);
        res.addAll(methodParam);
        return res.toArray(PsiReference.EMPTY_ARRAY);
    }

}
