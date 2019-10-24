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
import com.intellij.util.ArrayUtil;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.annotation.Annotation;
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

/**
 * foreach标签转换器
 *
 * @author : zhengrf
 * @date : 2019-06-27
 */
public class ForeachConverter {

    private static PsiElement resloaveProvider(final String text, final PsiMethod method) {
        PsiParameter[] parameters = (PsiParameter[]) method.getParameters();
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
        return PsiJavaUtils.findPsiMethod(psiClass, CompletionUtils.processTextToGetMethodName(text)).orElse(null);
    }

    private static void addPsiClassVariants(@NotNull final String prefix, @Nullable final PsiClass psiClass, final Set<String> res) {
        if (psiClass == null) {
            return;
        }
        for (PsiMethod info : psiClass.getAllMethods()) {
            if ((PsiTypeUtils.isCollectionType(info.getReturnType()) || PsiTypeUtils.isCustomType(info.getReturnType())) && PsiJavaUtils.isGetMethod(info)) {
                res.add(prefix + PsiJavaUtils.processGetMethodName(info));
            }
        }
        for (PsiField info : psiClass.getAllFields()) {
            if ((PsiTypeUtils.isCollectionType(info.getType()) || PsiTypeUtils.isCustomType(info.getType())) && CompletionUtils.isTargetField(info)) {
                res.add(prefix + info.getName());
            }
        }
    }

    public static class Collection extends ResolvingConverter.StringConverter implements VariantsProvider<Set<String>> {

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
            return classElement.getIdMethod()
                    .map(method -> provider(xmlAttributeValue.getValue(), CompletionUtils.getPrefixArr(CompletionUtils.getPrefixStr(xmlAttributeValue.getValue())), (PsiParameter[]) method.getParameters(), new HashSet<>()))
                    .orElse(Collections.emptySet());
        }

        @Nullable
        @Override
        public PsiElement resolve(String text, ConvertContext context) {
            if (StringUtil.isEmpty(text) || text.trim().endsWith(".")) {
                return null;
            }
            ClassElement classElement = DomUtils.getParentOfType(context.getInvocationElement(), ClassElement.class, true);
            if (classElement == null) {
                return null;
            }
            return classElement.getIdMethod().map(method -> resloaveProvider(text, method)).orElse(null);
        }

        @Nullable
        @Override
        public LookupElement createLookupElement(String s) {
            return LookupElementBuilder.create(s).withIcon(AllIcons.Nodes.Parameter);
        }

        @Override
        public void singleParam(@NotNull final String prefixText, @NotNull final String[] prefixArr, @NotNull final PsiParameter parameter, @NotNull final Set<String> res) {
            Annotation.Value value = Annotation.PARAM.getValue(parameter);
            if (value == null) {
                if (PsiTypeUtils.isCustomType(parameter.getType())) {
                    Optional.ofNullable(((PsiClassType) parameter.getType()).resolve()).ifPresent(psiClass -> addPsiClassVariants("", psiClass, res));
                } else if (PsiTypeUtils.isCollectionType(parameter.getType())) {
                    res.add("list");
                } else if (PsiTypeUtils.isArrayType(parameter.getType())) {
                    res.add("array");
                }
            } else {
                res.add(Annotation.PARAM.getValue(parameter, parameter::getName).getValue());
            }
        }

        @Override
        public void multiParam(@NotNull final String prefixText, @NotNull final String[] prefixArr, @NotNull final PsiParameter[] parameters, @NotNull final Set<String> res) {
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
        public void emptyPrefix(@NotNull final String prefixText, @NotNull final String[] prefixArr, @NotNull final PsiParameter[] parameters, @NotNull final Set<String> res) {
            final PsiClassType psiClassType = CompletionUtils.getPrefixPsiClass(prefixArr, CompletionUtils.getPrefixType(prefixArr[0], parameters));
            if (psiClassType != null) {
                addPsiClassVariants(String.join(",", prefixArr).concat("."), psiClassType.resolve(), res);
            }
        }

    }

}
