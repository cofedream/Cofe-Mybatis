/*
 * Copyright (C) 2019 cofe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mybatis.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.enums.AttributeEnums;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.EnumUtils;
import tk.cofe.plugin.mybatis.util.PsiMybatisUtils;
import tk.cofe.plugin.mybatis.util.PsiTypeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码完成,无需指向引用
 *
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class MybatisXmlCompletionContributor extends CompletionContributor {

    private static LookupElementBuilder createLookupElementBuilder(String lookupString, String typeText, String tailText) {
        return LookupElementBuilder.create(lookupString).withTypeText(typeText).withPresentableText("").appendTailText(tailText, true);
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        if (!PsiMybatisUtils.isElementWithMapperXMLFile(position)) {
            return;
        }
        XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(position, XmlAttribute.class);
        if (xmlAttribute == null) {
            return;
        }
        XmlTag xmlTag = PsiTreeUtil.getParentOfType(position, XmlTag.class, true);
        if (xmlTag == null) {
            return;
        }
        EnumUtils.parse(StatementAttribute.values(), xmlAttribute).ifPresent(statementAttribute -> statementAttribute.process(xmlTag, result));
    }

    private enum StatementAttribute implements AttributeEnums {
        RESULT_TYPE("resultType") {
            private final Map<String, List<LookupElementBuilder>> notCustomType = Collections.unmodifiableMap(new HashMap<String, List<LookupElementBuilder>>() {{
                this.put("byte", Collections.singletonList(createLookupElementBuilder("_byte", "byte", "byte")));
                this.put("long", Collections.singletonList(createLookupElementBuilder("_long", "long", "long")));
                this.put("short", Collections.singletonList(createLookupElementBuilder("_short", "short", "short")));
                this.put("int", new ArrayList<LookupElementBuilder>(2) {{
                    this.add(createLookupElementBuilder("_int", "int", "int"));
                    this.add(createLookupElementBuilder("_integer", "int", "int"));
                }});
                this.put("double", Collections.singletonList(createLookupElementBuilder("_double", "double", "double")));
                this.put("float", Collections.singletonList(createLookupElementBuilder("_float", "float", "float")));
                this.put("boolean", Collections.singletonList(createLookupElementBuilder("_boolean", "boolean", "boolean")));
                this.put("String", Collections.singletonList(createLookupElementBuilder("string", "String", "string")));
                this.put("Byte", Collections.singletonList(createLookupElementBuilder("byte", "Byte", "byte")));
                this.put("Long", Collections.singletonList(createLookupElementBuilder("long", "Long", "long")));
                this.put("Short", Collections.singletonList(createLookupElementBuilder("short", "Short", "short")));
                this.put("Integer", new ArrayList<LookupElementBuilder>(2) {{
                    this.add(createLookupElementBuilder("int", "Integer", "int"));
                    this.add(createLookupElementBuilder("integer", "Integer", "int"));
                }});
                this.put("Double", Collections.singletonList(createLookupElementBuilder("double", "Double", "double")));
                this.put("Float", Collections.singletonList(createLookupElementBuilder("float", "Float", "float")));
                this.put("Boolean", Collections.singletonList(createLookupElementBuilder("boolean", "Boolean", "boolean")));
                this.put("Date", Collections.singletonList(createLookupElementBuilder("date", "Date", "date")));
                this.put("Bigdecimal", new ArrayList<LookupElementBuilder>(2) {{
                    this.add(createLookupElementBuilder("decimal", "Bigdecimal", "decimal"));
                    this.add(createLookupElementBuilder("bigdecimal", "Bigdecimal", "decimal"));
                }});
                this.put("Object", Collections.singletonList(createLookupElementBuilder("object", "Object", "object")));
                this.put("Map", Collections.singletonList(createLookupElementBuilder("map", "Map", "map")));
                this.put("Hashmap", Collections.singletonList(createLookupElementBuilder("hashmap", "Hashmap", "hashmap")));
                this.put("List", Collections.singletonList(createLookupElementBuilder("list", "List", "list")));
                this.put("Arraylist", Collections.singletonList(createLookupElementBuilder("arraylist", "Arraylist", "arraylist")));
                this.put("Collection", Collections.singletonList(createLookupElementBuilder("collection", "Collection", "collection")));
                this.put("Iterator", Collections.singletonList(createLookupElementBuilder("iterator", "Iterator", "iterator")));
            }});

            @Override
            void process(XmlTag xmlTag, CompletionResultSet result) {
                ClassElement classElement = (ClassElement) DomUtils.getDomElement(xmlTag);
                if (classElement == null) {
                    return;
                }
                classElement.getIdMethod().ifPresent(psiMethod -> {
                    PsiType type = psiMethod.getReturnType();
                    if (type == null) {
                        return;
                    }
                    if (PsiTypeUtils.isPrimitiveOrBoxType(type) || PsiTypeUtils.isCollectionOrMapType(type)) {
                        result.addAllElements(notCustomType.get(type.getPresentableText()));
                    } else {
                        if (type instanceof PsiClassReferenceType) {
                            PsiJavaCodeReferenceElement reference = ((PsiClassReferenceType) type).getReference();
                            String referenceName = reference.getReferenceName();
                            if (StringUtil.isEmpty(referenceName)) {
                                return;
                            }
                            if (PsiTypeUtils.isPrimitiveOrBoxType(type) || PsiTypeUtils.isCollectionOrMapType(type)) {
                                result.addAllElements(notCustomType.get(referenceName));
                            } else {
                                result.addElement(createLookupElementBuilder(reference.getQualifiedName(), reference.getQualifiedName(), referenceName));
                            }
                        }
                    }

                });
                result.stopHere();
            }
        },
        JDBC_TYPE("jdbcType") {
            @Override
            void process(XmlTag xmlTag, CompletionResultSet resultSet) {

            }
        };

        private String value;

        StatementAttribute(String attributeValue) {
            this.value = attributeValue;
        }

        abstract void process(XmlTag xmlTag, CompletionResultSet resultSet);

        public String getValue() {
            return value;
        }
    }
}
