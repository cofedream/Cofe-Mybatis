/*
 * Copyright (C) 2019-2021 cofe
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

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.*;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.common.utils.PsiTypeUtils;
import tk.cofe.plugin.mybatis.config.MybatisConstants;
import tk.cofe.plugin.mybatis.dom.model.Mapper;
import tk.cofe.plugin.mybatis.dom.model.mix.CRUDMix;
import tk.cofe.plugin.mybatis.dom.model.tag.Delete;
import tk.cofe.plugin.mybatis.dom.model.tag.Insert;
import tk.cofe.plugin.mybatis.dom.model.tag.Select;
import tk.cofe.plugin.mybatis.dom.model.tag.Update;

import java.util.*;
import java.util.stream.Collectors;

/**
 * mybatis Psi工具类
 *
 * @author : zhengrf
 * @date : 2019-06-23
 */
public class MybatisUtils {
    private static final Map<String, List<String>> BASE_TYPE = Collections.unmodifiableMap(new HashMap<String, List<String>>() {
        private static final long serialVersionUID = -7375291625150519393L;

        {
            put("byte", "_byte");
            put("long", "_long");
            put("short", "_short");
            put("int", "_int", "_integer");
            put("double", "_double");
            put("float", "_float");
            put("boolean", "_boolean");
            put("String", "string");
            put("Byte", "byte");
            put("Long", "long");
            put("Short", "short");
            put("Integer", "int", "integer");
            put("Double", "double");
            put("Float", "float");
            put("Boolean", "boolean");
            put("Date", "date");
            put("Bigdecimal", "decimal", "bigdecimal");
            put("Object", "object");
            put("Map", "map");
            put("Hashmap", "hashmap");
            put("List", "list");
            put("Arraylist", "arraylist");
            put("Collection", "collection");
            put("Iterator", "iterator");
        }

        private void put(final String s, final String... values) {
            this.put(s, Arrays.asList(values));
        }
    });


    public static Optional<Mapper> getMapper(DomElement element) {
        return Optional.ofNullable(DomUtils.getParentOfType(element, Mapper.class));
    }

    public static Optional<Mapper> getMapper(XmlElement element) {
        DomManager domManager = DomManager.getDomManager(element.getProject());
        DomFileElement<Mapper> fileElement = domManager.getFileElement(((XmlFile) element.getContainingFile()), Mapper.class);
        return fileElement == null ? Optional.empty() : Optional.of(fileElement.getRootElement());
    }

    /**
     * 判断是否为mapper.xml文件
     *
     * @param file 文件
     * @return 结果
     */
    public static boolean isMapperXmlFile(PsiFile file) {
        if (!(file instanceof XmlFile)) {
            return false;
        }
        return isTargetXml(((XmlFile) file), MybatisConstants.MAPPER_NAME_SPACES);
    }

    public static boolean isElementWithMapperXMLFile(PsiElement element) {
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
    public static boolean isBaseStatement(final XmlElement xmlElement) {
        if (xmlElement == null) {
            return false;
        }
        DomElement domElement = DomUtils.getDomElement(xmlElement);
        if (!(domElement instanceof CRUDMix)) {
            return false;
        }
        return Mapper.BASIC_OPERATION.stream().anyMatch(clazz -> clazz.isInstance(domElement));
    }

    public static boolean isTargetXml(XmlFile xmlFile, String... namespaces) {
        if (namespaces == null || namespaces.length == 0) {
            return false;
        }
        Collection<String> namespacesSet = Arrays.asList(namespaces);
        XmlFileHeader header = DomService.getInstance().getXmlFileHeader(xmlFile);
        return namespacesSet.contains(header.getPublicId()) || namespacesSet.contains(header.getSystemId()) || namespacesSet.contains(header.getRootTagNamespace());
    }

    /**
     * 获取 ResultType
     *
     * @param type 类型
     */
    public static List<String> getResultType(PsiType type) {
        if (type == null) {
            return Collections.emptyList();
        } else if (PsiTypeUtils.isVoid(type)) {
            return Collections.emptyList();
        } else if (PsiTypeUtils.isPrimitiveOrBoxType(type)) {
            return Optional.ofNullable(BASE_TYPE.get(type.getPresentableText())).orElse(Collections.emptyList());
        } else if (PsiTypeUtils.isMapType(type)) {
            return Optional.ofNullable(BASE_TYPE.get(((PsiClassReferenceType) type).getClassName())).orElse(Collections.emptyList());
        } else if (PsiTypeUtils.isCollectionType(type)) {
            return Arrays.stream(((PsiClassReferenceType) type).getParameters()).map(PsiType::getCanonicalText).collect(Collectors.toList());
        } else {
            PsiJavaCodeReferenceElement reference = ((PsiClassReferenceType) type).getReference();
            return StringUtil.isEmpty(reference.getReferenceName()) ? Collections.emptyList() : Collections.singletonList(reference.getQualifiedName());
        }
    }

    /**
     * 获取当前 XML 对应的 Mapper 接口
     *
     * @param CRUDMix 基础CRUD元素
     * @return PsiClass
     */
    public static Optional<PsiClass> getPsiClass(CRUDMix CRUDMix) {
        return Optional.ofNullable(DomUtils.getParentOfType(CRUDMix, Mapper.class, true)).flatMap(Mapper::getNamespaceValue);
    }

    public static PsiLanguageInjectionHost getOriginElement(final PsiElement element) {
        return InjectedLanguageManager.getInstance(element.getProject()).getInjectionHost(element);
    }

    public static PsiLanguageInjectionHost getOriginElement(final CompletionParameters parameters) {
        return getOriginElement(parameters.getPosition());
    }
}
