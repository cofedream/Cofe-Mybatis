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
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.ArrayUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.CompletionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.PsiJavaUtils;
import tk.cofe.plugin.mybatis.util.PsiTypeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    public static class Collection extends ResolvingConverter.StringConverter {

        @NotNull
        @Override
        public java.util.Collection<? extends String> getVariants(ConvertContext context) {
            XmlAttribute xmlAttributeValue = (XmlAttribute) context.getXmlElement();
            if (xmlAttributeValue == null) {
                return Collections.emptySet();
            }
            ClassElement classElement = DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class);
            if (classElement == null) {
                return Collections.emptyList();
            }
            return classElement.getIdMethod().map(method -> {
                PsiParameter[] parameters = (PsiParameter[]) method.getParameters();
                if (ArrayUtil.isEmpty(parameters)) {
                    return Collections.<String>emptySet();
                }
                String[] prefixArr = getPrefixArr(CompletionUtils.getPrefixStr(xmlAttributeValue.getValue()));
                if (ArrayUtil.isEmpty(prefixArr)) {
                    if (parameters.length == 1) {
                        PsiParameter firstParam = parameters[0];
                        Annotation.Value value = Annotation.PARAM.getValue(firstParam);
                        if (value == null) {
                            if (PsiTypeUtils.isCustomType(firstParam.getType())) {
                                return Optional.ofNullable(((PsiClassType) firstParam.getType()).resolve()).map(ForeachConverter::addPsiClassVariants).orElse(Collections.emptyList());
                            } else if (PsiTypeUtils.isCollectionType(firstParam.getType())) {
                                return Collections.singletonList("list");
                            } else if (PsiTypeUtils.isArrayType(firstParam.getType())) {
                                return Collections.singletonList("array");
                            }
                        } else {
                            return Collections.singletonList(Annotation.PARAM.getValue(firstParam, firstParam::getName).getValue());
                        }
                    } else {
                        List<String> res = new ArrayList<>();
                        PsiParameterList parameterList = method.getParameterList();
                        for (int i = 0; i < parameterList.getParameters().length; i++) {
                            Annotation.Value value = Annotation.PARAM.getValue(parameterList.getParameters()[i]);
                            if (value != null) {
                                res.add(value.getValue());
                            } else {
                                res.add("param" + (i + 1));
                            }
                        }
                        return res;
                    }
                } else {
                    PsiType type = CompletionUtils.getPrefixType(prefixArr[0], parameters);
                    return addPsiClassVariants(String.join(",", prefixArr).concat("."), CompletionUtils.getTargetPsiClass(prefixArr, (PsiClassType) type));
                }
                return Collections.<String>emptySet();
            }).orElse(Collections.emptyList());
        }

        @Nullable
        @Override
        public PsiElement resolve(String text, ConvertContext context) {
            if (StringUtil.isEmpty(text)) {
                return null;
            }
            ClassElement classElement = DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class, true);
            if (classElement == null) {
                return null;
            }
            List<PsiParameter> parameters = classElement.getIdMethod()
                    .map(method -> Arrays.stream(method.getParameterList().getParameters()).filter(psiParameter -> text.equals(Annotation.PARAM.getValue(psiParameter, psiParameter::getName).getValue())).collect(Collectors.toList()))
                    .orElse(Collections.emptyList());
            if (parameters.isEmpty()) {
                return null;
            }
            return super.resolve(text, context);
        }

        @Nullable
        @Override
        public LookupElement createLookupElement(String s) {
            return LookupElementBuilder.create(s).withIcon(AllIcons.Nodes.Parameter);
        }
    }

    private static java.util.Collection<String> addPsiClassVariants(final PsiClass psiClass) {
        return addPsiClassVariants("", psiClass);
    }

    private static java.util.Collection<String> addPsiClassVariants(@NotNull final String prefix, final PsiClassType psiClassType) {
        if (psiClassType == null) {
            return Collections.emptyList();
        }
        return addPsiClassVariants(prefix, psiClassType.resolve());
    }

    private static java.util.Collection<String> addPsiClassVariants(@NotNull final String prefix, @Nullable final PsiClass psiClass) {
        if (psiClass == null) {
            return Collections.emptyList();
        }
        Set<String> res = new HashSet<>();
        for (PsiMethod info : psiClass.getAllMethods()) {
            if (PsiTypeUtils.isCollectionType(info.getReturnType()) && CompletionUtils.isTargetMethod(info)) {
                res.add(prefix + PsiJavaUtils.processGetMethodName(info));
            }
        }
        for (PsiField info : psiClass.getAllFields()) {
            if (PsiTypeUtils.isCollectionType(info.getType()) && CompletionUtils.isTargetField(info)) {
                res.add(prefix + info.getName());
            }
        }
        return res;
    }

    private static String[] getPrefixArr(@NotNull String prefix) {
        if (StringUtil.isEmpty(prefix) || !prefix.contains(".")) {
            return new String[0];
        }
        return prefix.substring(0, prefix.lastIndexOf(".")).split("\\.");
    }
}
