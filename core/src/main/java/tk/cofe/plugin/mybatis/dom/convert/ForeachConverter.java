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

package tk.cofe.plugin.mybatis.dom.convert;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ArrayUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Foreach;
import tk.cofe.plugin.mybatis.dom.model.include.BindInclude;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.provider.VariantsProvider;
import tk.cofe.plugin.mybatis.util.CompletionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;
import tk.cofe.plugin.mybatis.util.PsiTypeUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * foreach标签转换器
 *
 * @author : zhengrf
 * @date : 2019-06-27
 */
public class ForeachConverter {

    private static PsiElement resloaveProvider(final String text, final PsiMethod method) {
        PsiParameter[] parameters = method.getParameterList().getParameters();
        if (ArrayUtil.isEmpty(parameters)) {
            return null;
        }
        PsiElement res;
        String[] prefixArr = CompletionUtils.getPrefixArr(text);
        if (ArrayUtil.isEmpty(prefixArr)) {
            res = existPrefix(text, parameters);
        } else {
            res = emptyPrefix(text, parameters, prefixArr);
        }
        return res;
    }

    private static PsiElement existPrefix(final String text, final PsiParameter[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            Annotation.Value value = Annotation.PARAM.getValue(parameters[i]);
            if (value == null) {
                if (PsiTypeUtils.isCustomType(parameters[i].getType())) {
                    return Optional.ofNullable(((PsiClassType) parameters[i].getType()).resolve())
                            .map(psiClass -> ForeachConverter.resolve(psiClass, text))
                            .orElse(null);
                } else if (PsiTypeUtils.isCollectionType(parameters[i].getType())
                        || PsiTypeUtils.isArrayType(parameters[i].getType())) {
                    if (("param" + (i + 1)).equals(text)
                            || ((parameters.length == 1) && ("list".equals(text) || "array".equals(text)))) {
                        return parameters[i];
                    }
                }
            } else if ((Objects.equals(text, value.getValue()) || ("param" + (i + 1)).equals(text))
                    && (PsiTypeUtils.isCollectionType(parameters[i].getType())
                    || PsiTypeUtils.isArrayType(parameters[i].getType()))) {
                return parameters[i];
            }
        }
        return null;
    }

    private static PsiElement emptyPrefix(final String text, final PsiParameter[] parameters, final String[] prefixArr) {
        PsiType type = CompletionUtils.getPrefixType(prefixArr[0], parameters);
        for (int i = 1; i < prefixArr.length; i++) {
            type = CompletionUtils.getPrefixPsiType(prefixArr[i], type);
        }
        return type instanceof PsiClassType ? ForeachConverter.resolve(((PsiClassType) type).resolve(), text.substring(text.lastIndexOf(".") + 1)) : null;
    }

    @Nullable
    private static PsiElement resolve(@Nullable PsiClass psiClass, @Nullable String text) {
        if (psiClass == null || text == null) {
            return null;
        }
        PsiField field = psiClass.findFieldByName(text, true);
        if (field != null) {
            return field;
        }
        return PsiJavaUtils.findPsiMethod(psiClass, PsiJavaUtils.toGetPrefix(text)).orElse(null);
    }

    private static void addPsiClassVariants(@NotNull final String prefix, @Nullable final PsiClass psiClass, final Set<String> res) {
        PsiJavaUtils.psiClassProcessor(psiClass,
                field -> (PsiTypeUtils.isCollectionType(field.getType()) || PsiTypeUtils.isCustomType(field.getType())) && PsiJavaUtils.notSerialField(field),
                field -> res.add(prefix + field.getName()),
                method -> (PsiTypeUtils.isCollectionType(method.getReturnType()) || PsiTypeUtils.isCustomType(method.getReturnType())) && PsiJavaUtils.isGetMethod(method),
                method -> res.add(prefix + PsiJavaUtils.replaceGetPrefix(method)));
    }

    public static class Collection extends ResolvingConverter.StringConverter implements VariantsProvider<Set<String>> {

        @NotNull
        @Override
        public java.util.Collection<? extends String> getVariants(ConvertContext context) {
            XmlAttribute xmlAttribute = (XmlAttribute) context.getXmlElement();
            if (xmlAttribute == null) {
                return Collections.emptySet();
            }
            Set<String> res = new HashSet<>();
            // 添加Bind标签
            res.addAll(getBindTags(context.getTag()));
            // 添加Foreach标签
            res.addAll(getForeachTags(context.getTag()));
            // statement定义无对应方法
            ClassElement classElement = DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class);
            if (classElement != null) {
                res.addAll(classElement.getIdMethod()
                        .map(method -> provider(xmlAttribute.getValue(), CompletionUtils.getPrefixArr(CompletionUtils.getPrefixStr(xmlAttribute.getValue())), method.getParameterList().getParameters(), new HashSet<>()))
                        .orElse(Collections.emptySet()));
            }
            return res;
        }

        @NotNull
        private Set<String> getBindTags(@Nullable final XmlElement element) {
            return DomUtils.getParents(element, XmlTag.class, BindInclude.class).stream()
                    .flatMap(info -> info.getBinds().stream())
                    .map(info -> DomUtils.getAttributeVlaue(info.getName()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        @NotNull
        private Set<String> getForeachTags(@Nullable final XmlElement element) {
            return DomUtils.getParents(element, XmlTag.class, Foreach.class).stream()
                    .map(info -> DomUtils.getAttributeVlaue(info.getItem()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        @Nullable
        @Override
        public PsiElement resolve(String text, ConvertContext context) {
            if (StringUtil.isEmpty(text) || text.trim().endsWith(".")) {
                return null;
            }
            final DomElement element = context.getInvocationElement();
            final XmlElement xmlElement = element.getXmlElement();
            final XmlAttributeValue bindName = getBindName(text, xmlElement);
            if (bindName != null) {
                return bindName;
            }
            final XmlAttributeValue item = getItem(text, xmlElement);
            if (item != null) {
                return item;
            }
            ClassElement classElement = DomUtils.getParentOfType(element, ClassElement.class, true);
            if (classElement == null) {
                return null;
            }
            return classElement.getIdMethod().map(method -> resloaveProvider(text, method)).orElse(null);
        }

        private XmlAttributeValue getBindName(final String text, final XmlElement xmlElement) {
            return DomUtils.getParents(xmlElement, XmlTag.class, BindInclude.class).stream()
                    .flatMap(info -> info.getBinds().stream())
                    .filter(info -> DomUtils.getAttributeVlaue(info.getName()).map(name -> Objects.equals(name, text)).orElse(false))
                    .map(bind -> bind.getName().getXmlAttributeValue())
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }

        private XmlAttributeValue getItem(final String text, final XmlElement xmlElement) {
            return DomUtils.getParents(xmlElement, XmlTag.class, Foreach.class).stream()
                    .filter(info -> DomUtils.getAttributeVlaue(info.getItem()).map(name -> Objects.equals(name, text)).orElse(false))
                    .map(Foreach::getItem)
                    .filter(Objects::nonNull)
                    .map(GenericAttributeValue::getXmlAttributeValue)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }

        @Nullable
        @Override
        public LookupElement createLookupElement(String s) {
            return LookupElementBuilder.create(s).withIcon(AllIcons.Nodes.Parameter);
        }

        @Override
        public void singleParam(final String prefixText, final String[] prefixArr, final PsiParameter firstParameter, final Set<String> res) {
            Annotation.Value value = Annotation.PARAM.getValue(firstParameter);
            if (value == null) {
                if (PsiTypeUtils.isCustomType(firstParameter.getType())) {
                    Optional.ofNullable(((PsiClassType) firstParameter.getType()).resolve()).ifPresent(psiClass -> addPsiClassVariants("", psiClass, res));
                } else if (PsiTypeUtils.isCollectionType(firstParameter.getType())) {
                    res.add("list");
                } else if (PsiTypeUtils.isArrayType(firstParameter.getType())) {
                    res.add("array");
                }
            } else {
                res.add(Annotation.PARAM.getValue(firstParameter, firstParameter::getName).getValue());
            }
        }

        @Override
        public void multiParam(final String prefixText, final String[] prefixArr, final PsiParameter[] parameters, final Set<String> res) {
            for (int i = 0; i < parameters.length; i++) {
                Annotation.Value value = Annotation.PARAM.getValue(parameters[i]);
                if (value == null) {
                    res.add("param" + (i + 1));
                } else {
                    res.add(value.getValue());
                }
            }
        }

        @Override
        public void emptyPrefix(final String prefixText, final String[] prefixArr, final PsiParameter[] parameters, final Set<String> res) {
            final PsiClassType psiClassType = CompletionUtils.getPrefixPsiClass(prefixArr, CompletionUtils.getPrefixType(prefixArr[0], parameters));
            if (psiClassType != null) {
                addPsiClassVariants(String.join(",", prefixArr).concat("."), psiClassType.resolve(), res);
            }
        }

        private static class ComplexPsiElement {
            private String text;
            private PsiElement element;

            public ComplexPsiElement(final String text, final PsiElement element) {
                this.text = text;
                this.element = element;
            }

            public String getText() {
                return text;
            }

            public PsiElement getElement() {
                return element;
            }
        }

    }

}
