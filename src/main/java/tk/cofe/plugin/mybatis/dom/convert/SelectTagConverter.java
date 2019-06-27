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
import tk.cofe.plugin.mybatis.util.PsiTypeUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Select 标签相关转换
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class SelectTagConverter {

    public static class ResultType extends ResolvingConverter<String> {

        private final Map<String, LookupElementBuilder> notCustomType = Collections.unmodifiableMap(new HashMap<String, LookupElementBuilder>() {
            private static final long serialVersionUID = 4402307158708442334L;

            {
                this.put("_byte", createLookupElementBuilder("_byte", "byte", "byte"));
                this.put("_long", createLookupElementBuilder("_long", "long", "long"));
                this.put("_short", createLookupElementBuilder("_short", "short", "short"));
                this.put("_int", createLookupElementBuilder("_int", "int", "int"));
                this.put("_integer", createLookupElementBuilder("_integer", "int", "int"));
                this.put("_double", createLookupElementBuilder("_double", "double", "double"));
                this.put("_float", createLookupElementBuilder("_float", "float", "float"));
                this.put("_boolean", createLookupElementBuilder("_boolean", "boolean", "boolean"));
                this.put("string", createLookupElementBuilder("string", "String", "string"));
                this.put("byte", createLookupElementBuilder("byte", "Byte", "byte"));
                this.put("long", createLookupElementBuilder("long", "Long", "long"));
                this.put("short", createLookupElementBuilder("short", "Short", "short"));
                this.put("int", createLookupElementBuilder("int", "Integer", "int"));
                this.put("integer", createLookupElementBuilder("integer", "Integer", "int"));
                this.put("Double", createLookupElementBuilder("double", "Double", "double"));
                this.put("Float", createLookupElementBuilder("float", "Float", "float"));
                this.put("Boolean", createLookupElementBuilder("boolean", "Boolean", "boolean"));
                this.put("Date", createLookupElementBuilder("date", "Date", "date"));
                this.put("decimal", createLookupElementBuilder("decimal", "Bigdecimal", "decimal"));
                this.put("bigdecimal", createLookupElementBuilder("bigdecimal", "Bigdecimal", "decimal"));
                this.put("Object", createLookupElementBuilder("object", "Object", "object"));
                this.put("Map", createLookupElementBuilder("map", "Map", "map"));
                this.put("Hashmap", createLookupElementBuilder("hashmap", "Hashmap", "hashmap"));
                this.put("List", createLookupElementBuilder("list", "List", "list"));
                this.put("Arraylist", createLookupElementBuilder("arraylist", "Arraylist", "arraylist"));
                this.put("Collection", createLookupElementBuilder("collection", "Collection", "collection"));
                this.put("Iterator", createLookupElementBuilder("iterator", "Iterator", "iterator"));
            }
        });

        private static LookupElementBuilder createLookupElementBuilder(String lookupString, String typeText, String tailText) {
            return LookupElementBuilder.create(lookupString).withTypeText(typeText).withPresentableText(Empty.STRING).appendTailText(tailText, true);
        }

        @NotNull
        @Override
        public Collection<? extends String> getVariants(ConvertContext context) {
            return JavaPsiService.getInstance(context.getProject()).findPsiMethod((Select) DomUtils.getDomElement(context.getTag())).map(psiMethod -> PsiTypeUtils.getResultType(psiMethod.getReturnType())).orElse(Collections.emptyList());
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
            LookupElementBuilder builder = notCustomType.get(text);
            if (builder != null) {
                return builder;
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
