package tk.cofedream.plugin.mybatis.dom.mapper.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Mapper;
import tk.cofedream.plugin.mybatis.dom.mapper.model.tag.Select;
import tk.cofedream.plugin.mybatis.service.JavaPsiService;
import tk.cofedream.plugin.mybatis.utils.PsiTypeUtil;
import tk.cofedream.plugin.mybatis.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2019-01-21
 */
public class SelectTagConverter {

    public static class ResultType extends ResolvingConverter<String> {

        private static final Map<String, List<String>> BaseType = Collections.unmodifiableMap(new HashMap<String, List<String>>() {{
            this.put("byte", Collections.singletonList("_byte"));
            this.put("long", Collections.singletonList("_long"));
            this.put("short", Collections.singletonList("_short"));
            this.put("int", new ArrayList<String>(2) {{
                this.add("_int");
                this.add("_integer");
            }});
            this.put("double", Collections.singletonList("_double"));
            this.put("float", Collections.singletonList("_float"));
            this.put("boolean", Collections.singletonList("_boolean"));
            this.put("String", Collections.singletonList("string"));
            this.put("Byte", Collections.singletonList("byte"));
            this.put("Long", Collections.singletonList("long"));
            this.put("Short", Collections.singletonList("short"));
            this.put("Integer", new ArrayList<String>(2) {{
                this.add("int");
                this.add("integer");
            }});
            this.put("Double", Collections.singletonList("double"));
            this.put("Float", Collections.singletonList("float"));
            this.put("Boolean", Collections.singletonList("boolean"));
            this.put("Date", Collections.singletonList("date"));
            this.put("Bigdecimal", new ArrayList<String>(2) {{
                this.add("decimal");
                this.add("bigdecimal");
            }});
            this.put("Object", Collections.singletonList("object"));
            this.put("Map", Collections.singletonList("map"));
            this.put("Hashmap", Collections.singletonList("hashmap"));
            this.put("List", Collections.singletonList("list"));
            this.put("Arraylist", Collections.singletonList("arraylist"));
            this.put("Collection", Collections.singletonList("collection"));
            this.put("Iterator", Collections.singletonList("iterator"));
        }});
        private final Map<String, LookupElementBuilder> notCustomType = Collections.unmodifiableMap(new HashMap<String, LookupElementBuilder>() {{
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
        }});

        private static LookupElementBuilder createLookupElementBuilder(String lookupString, String typeText, String tailText) {
            return LookupElementBuilder.create(lookupString).withTypeText(typeText).withPresentableText("").appendTailText(tailText, true);
        }

        @NotNull
        @Override
        public Collection<? extends String> getVariants(ConvertContext context) {
            Select select = (Select) DomUtil.getDomElement(context.getTag());
            ArrayList<String> result = new ArrayList<>();
            JavaPsiService.getInstance(context.getProject()).findMethod(select).ifPresent(psiMethods -> {
                PsiType type = psiMethods[0].getReturnType();
                if (type == null) {
                    return;
                }
                if (PsiTypeUtil.notCustomType(type)) {
                    result.addAll(BaseType.get(type.getPresentableText()));
                } else {
                    if (type instanceof PsiClassReferenceType) {
                        PsiJavaCodeReferenceElement reference = ((PsiClassReferenceType) type).getReference();
                        String referenceName = reference.getReferenceName();
                        if (StringUtils.isBlank(referenceName)) {
                            return;
                        }
                        if (PsiTypeUtil.notCustomType(referenceName)) {
                            result.addAll(BaseType.get(referenceName));
                        } else {
                            result.add(reference.getQualifiedName());
                        }
                    }
                }
            });
            return result;
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
        public LookupElement createLookupElement(String s) {
            LookupElementBuilder builder = notCustomType.get(s);
            if (builder != null) {
                return builder;
            }
            String shortName = s;
            if (s.lastIndexOf(".") > 0) {
                shortName = s.substring(s.lastIndexOf(".") + 1);
            }
            return createLookupElementBuilder(s, s, shortName);
        }
    }

    public static class ResultMap extends XmlAttributeValueConverter<tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ResultMap> {
        @Nullable
        @Override
        protected Collection<XmlAttributeValue> getVariants(ConvertContext context, Mapper mapper) {
            return mapper.getResultMaps().stream().map(resultMap -> resultMap.getId().getXmlAttributeValue()).collect(Collectors.toList());
        }

        @Override
        protected boolean isTarget(@NotNull ConvertContext selfContext) {
            return selfContext.getInvocationElement().getParent() instanceof Select;
        }

        @Override
        protected List<? extends DomElement> getReferenceDomElements(@NotNull String value, @NotNull ConvertContext context, @NotNull Mapper mapper) {
            return mapper.getResultMaps();
        }

        @Override
        protected boolean filterDomElement(@NotNull tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ResultMap targetDomElement, @NotNull String selfValue) {
            return targetDomElement.getIdValue().map(id -> id.equals(selfValue)).orElse(false);
        }

        @Override
        protected XmlAttributeValue getTargetElement(@NotNull tk.cofedream.plugin.mybatis.dom.mapper.model.tag.ResultMap targetDomElement) {
            return targetDomElement.getId().getXmlAttributeValue();
        }
    }
}
