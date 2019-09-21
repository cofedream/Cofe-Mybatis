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
import com.intellij.psi.PsiMethod;
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

import java.util.Arrays;
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
public class MapperXmlCompletionContributor extends CompletionContributor {

    private static LookupElementBuilder createLookupElementBuilder(String lookupString) {
        return LookupElementBuilder.create(lookupString);
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
            private final Map<String, List<LookupElementBuilder>> registerType;
            {
                HashMap<String, List<LookupElementBuilder>> type = new HashMap<>();
                type.put("byte", Collections.singletonList(createLookupElementBuilder("_byte")));
                type.put("long", Collections.singletonList(createLookupElementBuilder("_long")));
                type.put("short", Collections.singletonList(createLookupElementBuilder("_short")));
                type.put("int", Arrays.asList(createLookupElementBuilder("_int"), createLookupElementBuilder("_integer")));
                type.put("double", Collections.singletonList(createLookupElementBuilder("_double")));
                type.put("float", Collections.singletonList(createLookupElementBuilder("_float")));
                type.put("boolean", Collections.singletonList(createLookupElementBuilder("_boolean")));
                type.put("String", Arrays.asList(createLookupElementBuilder("string"), createLookupElementBuilder("java.lang.String")));
                type.put("Byte", Arrays.asList(createLookupElementBuilder("byte"), createLookupElementBuilder("java.lang.Byte")));
                type.put("Long", Arrays.asList(createLookupElementBuilder("long"), createLookupElementBuilder("java.lang.Long")));
                type.put("Short", Arrays.asList(createLookupElementBuilder("short"), createLookupElementBuilder("java.lang.Short")));
                type.put("Integer", Arrays.asList(createLookupElementBuilder("int"), createLookupElementBuilder("integer"), createLookupElementBuilder("java.lang.Integer")));
                type.put("Double", Arrays.asList(createLookupElementBuilder("double"), createLookupElementBuilder("java.lang.Double")));
                type.put("Float", Arrays.asList(createLookupElementBuilder("float"), createLookupElementBuilder("java.lang.Float")));
                type.put("Boolean", Arrays.asList(createLookupElementBuilder("boolean"), createLookupElementBuilder("java.lang.Boolean")));
                type.put("Date", Arrays.asList(createLookupElementBuilder("date"), createLookupElementBuilder("java.util.Date")));
                type.put("Bigdecimal", Arrays.asList(createLookupElementBuilder("decimal"), createLookupElementBuilder("bigdecimal"), createLookupElementBuilder("java.math.BigDecimal")));
                type.put("Object", Arrays.asList(createLookupElementBuilder("object"), createLookupElementBuilder("java.lang.Object")));
                type.put("Map", Arrays.asList(createLookupElementBuilder("map"), createLookupElementBuilder("java.util.Map")));
                type.put("Hashmap", Arrays.asList(createLookupElementBuilder("hashmap"), createLookupElementBuilder("java.util.HashMap")));
                type.put("List", Arrays.asList(createLookupElementBuilder("list"), createLookupElementBuilder("java.util.List")));
                type.put("Arraylist", Arrays.asList(createLookupElementBuilder("arraylist"), createLookupElementBuilder("java.util.ArrayList")));
                type.put("Collection", Arrays.asList(createLookupElementBuilder("collection"), createLookupElementBuilder("java.util.Collection")));
                type.put("Iterator", Arrays.asList(createLookupElementBuilder("iterator"), createLookupElementBuilder("java.util.Iterator")));
                registerType = Collections.unmodifiableMap(type);
            }

            @Override
            void process(XmlTag xmlTag, CompletionResultSet result) {
                ClassElement classElement = (ClassElement) DomUtils.getDomElement(xmlTag);
                if (classElement == null) {
                    return;
                }
                classElement.getIdMethod()
                        .filter(info->info.getReturnType()!=null)
                        .map(PsiMethod::getReturnType)
                        .ifPresent(type -> {
                    if (PsiTypeUtils.isPrimitiveOrBoxType(type) || PsiTypeUtils.isCollectionOrMapType(type)) {
                        result.addAllElements(registerType.get(type.getPresentableText()));
                    } else {
                        if (type instanceof PsiClassReferenceType) {
                            PsiJavaCodeReferenceElement reference = ((PsiClassReferenceType) type).getReference();
                            String referenceName = reference.getReferenceName();
                            if (StringUtil.isEmpty(referenceName)) {
                                return;
                            }
                            if (PsiTypeUtils.isPrimitiveOrBoxType(type) || PsiTypeUtils.isCollectionOrMapType(type)) {
                                result.addAllElements(registerType.get(referenceName));
                            } else {
                                result.addElement(createLookupElementBuilder(reference.getQualifiedName()));
                            }
                        }
                    }

                });
                result.stopHere();
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
