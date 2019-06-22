package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
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
import tk.cofe.plugin.mybatis.util.StringUtils;

import java.util.ArrayList;
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

        private static final Map<String, List<String>> BaseType = Collections.unmodifiableMap(new HashMap<String, List<String>>() {
            private static final long serialVersionUID = -7375291625150519393L;

            {
                this.put("byte", Collections.singletonList("_byte"));
                this.put("long", Collections.singletonList("_long"));
                this.put("short", Collections.singletonList("_short"));
                this.put("int", new ArrayList<String>(2) {
                    private static final long serialVersionUID = -4321456431687868856L;

                    {
                        this.add("_int");
                        this.add("_integer");
                    }
                });
                this.put("double", Collections.singletonList("_double"));
                this.put("float", Collections.singletonList("_float"));
                this.put("boolean", Collections.singletonList("_boolean"));
                this.put("String", Collections.singletonList("string"));
                this.put("Byte", Collections.singletonList("byte"));
                this.put("Long", Collections.singletonList("long"));
                this.put("Short", Collections.singletonList("short"));
                this.put("Integer", new ArrayList<String>(2) {
                    private static final long serialVersionUID = -1451201046250936934L;

                    {
                        this.add("int");
                        this.add("integer");
                    }
                });
                this.put("Double", Collections.singletonList("double"));
                this.put("Float", Collections.singletonList("float"));
                this.put("Boolean", Collections.singletonList("boolean"));
                this.put("Date", Collections.singletonList("date"));
                this.put("Bigdecimal", new ArrayList<String>(2) {
                    private static final long serialVersionUID = -304742509443073751L;

                    {
                        this.add("decimal");
                        this.add("bigdecimal");
                    }
                });
                this.put("Object", Collections.singletonList("object"));
                this.put("Map", Collections.singletonList("map"));
                this.put("Hashmap", Collections.singletonList("hashmap"));
                this.put("List", Collections.singletonList("list"));
                this.put("Arraylist", Collections.singletonList("arraylist"));
                this.put("Collection", Collections.singletonList("collection"));
                this.put("Iterator", Collections.singletonList("iterator"));
            }
        });
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
            return JavaPsiService.getInstance(context.getProject()).findPsiMethod((Select) DomUtils.getDomElement(context.getTag())).map(psiMethod -> {
                PsiType type = psiMethod.getReturnType();
                if (type == null) {
                    return Collections.<String>emptyList();
                }
                if (PsiTypeUtils.isJavaBuiltInType(type)) {
                    return BaseType.get(type.getPresentableText());
                } else {
                    PsiJavaCodeReferenceElement reference = ((PsiClassReferenceType) type).getReference();
                    if (StringUtils.isBlank(reference.getReferenceName())) {
                        return Collections.<String>emptyList();
                    }
                    return Collections.singletonList(reference.getQualifiedName());
                }
            }).orElse(Collections.emptyList());
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
