package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.constants.Empty;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Select;
import tk.cofe.plugin.mybatis.service.JavaPsiService;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Select 标签相关转换
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class SelectTagConverter {

    public static class ResultType extends ResolvingConverter<String> {

        private static LookupElementBuilder createLookupElementBuilder(String lookupString, String typeText, String tailText) {
            return LookupElementBuilder.create(lookupString).withTypeText(typeText).withPresentableText(Empty.STRING).appendTailText(tailText, true);
        }

        @NotNull
        @Override
        public Collection<? extends String> getVariants(ConvertContext context) {
            return JavaPsiService.getInstance(context.getProject()).findPsiMethod((Select) DomUtils.getDomElement(context.getTag())).map(psiMethod -> PsiMybatisUtils.getResultType(psiMethod.getReturnType())).orElse(Collections.emptyList());
        }

        @Nullable
        @Override
        public String fromString(@Nullable String s, ConvertContext context) {
            return s;
        }

        @Nullable
        @Override
        public String toString(@Nullable String s, ConvertContext context) {
            return s;
        }

        @Nullable
        @Override
        public LookupElement createLookupElement(String text) {
            LookupElement lookupElement = PsiMybatisUtils.getResultTypeLookupElement(text);
            if (lookupElement != null) {
                return lookupElement;
            }
            String shortName = text;
            if (text.lastIndexOf(".") > 0) {
                shortName = text.substring(text.lastIndexOf(".") + 1);
            }
            return createLookupElementBuilder(text, text, shortName).withIcon(AllIcons.Nodes.Class);
        }
    }

    public static class ResultMap extends XmlAttributeValueConverter<tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap> {
        @Nullable
        @Override
        protected Collection<XmlAttributeValue> getVariants(ConvertContext context, Mapper mapper) {
            return mapper.getResultMaps().stream().filter(resultMap -> resultMap.getId() != null).map(resultMap -> resultMap.getId().getXmlAttributeValue()).collect(Collectors.toList());
        }

        @Override
        protected boolean isTarget(@NotNull ConvertContext selfContext) {
            return selfContext.getInvocationElement().getParent() instanceof Select;
        }

        @NotNull
        @Override
        protected List<tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper) {
            return mapper.getResultMaps();
        }

        @Override
        protected boolean filterDomElement(@NotNull tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap targetDomElement, @NotNull String selfValue) {
            return targetDomElement.getIdValue().map(id -> id.equals(selfValue)).orElse(false);
        }

        @Nullable
        @Override
        protected XmlAttributeValue getTargetElement(@NotNull tk.cofe.plugin.mybatis.dom.description.model.tag.ResultMap targetDomElement) {
            return Optional.ofNullable(targetDomElement.getId()).map(GenericAttributeValue::getXmlAttributeValue).orElse(null);
        }

    }
}
