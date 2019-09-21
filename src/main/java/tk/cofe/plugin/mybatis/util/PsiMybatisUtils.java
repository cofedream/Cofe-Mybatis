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

package tk.cofe.plugin.mybatis.util;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomService;
import com.intellij.util.xml.XmlFileHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.constants.Empty;
import tk.cofe.plugin.mybatis.constants.Mybatis;
import tk.cofe.plugin.mybatis.dom.description.model.Mapper;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Delete;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Insert;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Select;
import tk.cofe.plugin.mybatis.dom.description.model.tag.Update;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * mybatis Psi工具类
 *
 * @author : zhengrf
 * @date : 2019-06-23
 */
public class PsiMybatisUtils {
    private static final Map<String, List<String>> BaseType = Collections.unmodifiableMap(new HashMap<String, List<String>>() {
        private static final long serialVersionUID = -7375291625150519393L;

        {
            this.put("byte", Collections.singletonList("_byte"));
            this.put("long", Collections.singletonList("_long"));
            this.put("short", Collections.singletonList("_short"));
            this.put("int", Arrays.asList("_int", "_integer"));
            this.put("double", Collections.singletonList("_double"));
            this.put("float", Collections.singletonList("_float"));
            this.put("boolean", Collections.singletonList("_boolean"));
            this.put("String", Collections.singletonList("string"));
            this.put("Byte", Collections.singletonList("byte"));
            this.put("Long", Collections.singletonList("long"));
            this.put("Short", Collections.singletonList("short"));
            this.put("Integer", Arrays.asList("int", "integer"));
            this.put("Double", Collections.singletonList("double"));
            this.put("Float", Collections.singletonList("float"));
            this.put("Boolean", Collections.singletonList("boolean"));
            this.put("Date", Collections.singletonList("date"));
            this.put("Bigdecimal", Arrays.asList("decimal", "bigdecimal"));
            this.put("Object", Collections.singletonList("object"));
            this.put("Map", Collections.singletonList("map"));
            this.put("Hashmap", Collections.singletonList("hashmap"));
            this.put("List", Collections.singletonList("list"));
            this.put("Arraylist", Collections.singletonList("arraylist"));
            this.put("Collection", Collections.singletonList("collection"));
            this.put("Iterator", Collections.singletonList("iterator"));
        }
    });
    private static final Map<String, LookupElement> resultTypeLookupElement = Collections.unmodifiableMap(new HashMap<String, LookupElement>() {
        private static final long serialVersionUID = 4402307158708442334L;

        {
            this.put("_byte", createLookupElement("_byte", "byte", "byte"));
            this.put("_long", createLookupElement("_long", "long", "long"));
            this.put("_short", createLookupElement("_short", "short", "short"));
            this.put("_int", createLookupElement("_int", "int", "int"));
            this.put("_integer", createLookupElement("_integer", "int", "int"));
            this.put("_double", createLookupElement("_double", "double", "double"));
            this.put("_float", createLookupElement("_float", "float", "float"));
            this.put("_boolean", createLookupElement("_boolean", "boolean", "boolean"));
            this.put("string", createLookupElement("string", "String", "string"));
            this.put("byte", createLookupElement("byte", "Byte", "byte"));
            this.put("long", createLookupElement("long", "Long", "long"));
            this.put("short", createLookupElement("short", "Short", "short"));
            this.put("int", createLookupElement("int", "Integer", "int"));
            this.put("integer", createLookupElement("integer", "Integer", "int"));
            this.put("Double", createLookupElement("double", "Double", "double"));
            this.put("Float", createLookupElement("float", "Float", "float"));
            this.put("Boolean", createLookupElement("boolean", "Boolean", "boolean"));
            this.put("Date", createLookupElement("date", "Date", "date"));
            this.put("decimal", createLookupElement("decimal", "Bigdecimal", "decimal"));
            this.put("bigdecimal", createLookupElement("bigdecimal", "Bigdecimal", "decimal"));
            this.put("Object", createLookupElement("object", "Object", "object"));
            this.put("Map", createLookupElement("map", "Map", "map"));
            this.put("Hashmap", createLookupElement("hashmap", "Hashmap", "hashmap"));
            this.put("List", createLookupElement("list", "List", "list"));
            this.put("Arraylist", createLookupElement("arraylist", "Arraylist", "arraylist"));
            this.put("Collection", createLookupElement("collection", "Collection", "collection"));
            this.put("Iterator", createLookupElement("iterator", "Iterator", "iterator"));
        }
    });

    @Nullable
    public static Mapper getMapper(@NotNull DomElement element) {
        return DomUtils.getParentOfType(element, Mapper.class, true);
    }

    @Nullable
    public static Mapper getMapper(@NotNull XmlElement element) {
        DomManager domManager = DomManager.getDomManager(element.getProject());
        DomFileElement<Mapper> fileElement = domManager.getFileElement(((XmlFile) element.getContainingFile()), Mapper.class);
        return fileElement == null ? null : fileElement.getRootElement();
    }

    /**
     * 判断是否为mapper.xml文件
     *
     * @param file 文件
     * @return 结果
     */
    public static boolean isMapperXmlFile(@Nullable PsiFile file) {
        if (!(file instanceof XmlFile)) {
            return false;
        }
        return isTargetXml(((XmlFile) file), Mybatis.Mapper.DTDS);
    }

    public static boolean isElementWithMapperXMLFile(@Nullable PsiElement element) {
        return element instanceof XmlElement && isMapperXmlFile(element.getContainingFile());
    }

    /**
     * 基础 增删拆改操作
     *
     * @param xmlElement 元素
     * @return 判断是否增删查该操作标签内的元素
     * @see Select
     * @see Update
     * @see Delete
     * @see Insert
     */
    public static boolean isBaseStatementElement(@Nullable final XmlElement xmlElement) {
        if (xmlElement == null) {
            return false;
        }
        DomElement domElement = DomUtils.getDomElement(xmlElement);
        if (!(domElement instanceof ClassElement)) {
            return false;
        }
        return Mapper.BASIC_OPERATION.stream().anyMatch(clazz -> clazz.isInstance(domElement));
    }

    public static boolean isTargetXml(@NotNull XmlFile xmlFile, @Nullable String... namespaces) {
        if (namespaces == null || namespaces.length == 0) {
            return false;
        }
        Collection<String> namespacesSet = Arrays.asList(namespaces);
        XmlFileHeader header = DomService.getInstance().getXmlFileHeader(xmlFile);
        return namespacesSet.contains(header.getPublicId()) || namespacesSet.contains(header.getSystemId()) || namespacesSet.contains(header.getRootTagNamespace());
    }

    @Nullable
    public static DomElement getDomElement(@NotNull XmlElement xmlElement) {
        if (xmlElement instanceof XmlTag) {
            return DomManager.getDomManager(xmlElement.getProject()).getDomElement(((XmlTag) xmlElement));
        } else if (xmlElement instanceof XmlAttribute) {
            return DomManager.getDomManager(xmlElement.getProject()).getDomElement(((XmlAttribute) xmlElement));
        }
        return null;
    }

    /**
     * 获取 ResultType
     *
     * @param type 类型
     */
    @NotNull
    public static List<String> getResultType(@Nullable PsiType type) {
        if (type == null) {
            return Collections.emptyList();
        } else if (PsiPrimitiveType.VOID.equals(type)) {
            return Collections.emptyList();
        } else if (PsiTypeUtils.isPrimitiveOrBoxType(type)) {
            return Optional.ofNullable(BaseType.get(type.getPresentableText())).orElse(Collections.emptyList());
        } else if (PsiTypeUtils.isMapType(type)) {
            return Optional.ofNullable(BaseType.get(((PsiClassReferenceType) type).getClassName())).orElse(Collections.emptyList());
        } else if (PsiTypeUtils.isCollectionType(type)) {
            return Arrays.stream(((PsiClassReferenceType) type).getParameters()).map(PsiType::getCanonicalText).collect(Collectors.toList());
        } else {
            PsiJavaCodeReferenceElement reference = ((PsiClassReferenceType) type).getReference();
            return StringUtil.isEmpty(reference.getReferenceName()) ? Collections.emptyList() : Collections.singletonList(reference.getQualifiedName());
        }
    }

    /**
     * 获取 ResultType LookupElement
     *
     * @param type 类型
     * @return LookupElement
     */
    public static LookupElement getResultTypeLookupElement(@Nullable String type) {
        return resultTypeLookupElement.get(type);
    }

    private static LookupElement createLookupElement(String lookupString, String typeText, String tailText) {
        return LookupElementBuilder.create(lookupString).withTypeText(typeText).withPresentableText(Empty.STRING).appendTailText(tailText, true);
    }

    /**
     * 获取当前 XML 对应的 Mapper 接口
     *
     * @param classElement 基础CRUD元素
     * @return PsiClass
     */
    public static Optional<PsiClass> getPsiClass(@NotNull ClassElement classElement) {
        return Optional.ofNullable(DomUtils.getParentOfType(classElement, Mapper.class, true)).flatMap(Mapper::getNamespaceValue);
    }
}
