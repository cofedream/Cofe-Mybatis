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

package tk.cofe.plugin.mybatis.psi.reference;

import com.intellij.codeInsight.completion.CompletionUtilCore;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.util.ArrayUtil;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.CompletionUtils;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.common.utils.PsiMethodUtils;
import tk.cofe.plugin.common.utils.PsiTypeUtils;
import tk.cofe.plugin.mognl.psi.MOgnlDotReference;
import tk.cofe.plugin.mognl.psi.MOgnlIdentifierReference;
import tk.cofe.plugin.mybatis.dom.model.attirubte.NameAttribute;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Bind;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Foreach;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.MybatisXMLUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : zhengrf
 * @date : 2020-01-19
 */
public class MbELReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull final ProcessingContext context) {
        if (element.getTextLength() <= 0) {
            return PsiReference.EMPTY_ARRAY;
        }
        final PsiElement originElement = MybatisXMLUtils.getOriginElement(element);
        if (originElement == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        String text = element.getText().replace(CompletionUtilCore.DUMMY_IDENTIFIER, "");
        final String[] splitTextArr = text.split("\\.");
        // 方法参数
        return build(element, originElement, splitTextArr).toArray(PsiReference.EMPTY_ARRAY);
    }

    private List<PsiReference> build(@NotNull final PsiElement element, final PsiElement xmlElement, final String[] prefixArr) {
        ClassElement classElement = DomUtils.getDomElement(xmlElement, ClassElement.class).orElse(null);
        if (classElement == null) {
            return Collections.emptyList();
        }
        PsiMethod psiMethod = classElement.getIdMethod().orElse(null);
        if (psiMethod == null) {
            return Collections.emptyList();
        }
        List<PsiReference> references = new ArrayList<>(prefixArr.length + 1);
        String prefix0 = prefixArr[0]; // 第一个参数
        // 先查询XML标签
        final List<PsiElement> resolveResults = new ArrayList<>();
        for (Bind bind : MybatisXMLUtils.getTheBindTagInParents(xmlElement)) {
            if (Objects.equals(DomUtils.getAttributeValue(bind.getName()), prefix0)) {
                Optional.ofNullable(bind.getName())
                        .map(GenericAttributeValue::getXmlAttributeValue)
                        .ifPresent(resolveResults::add);
            }
        }
        for (Foreach foreach : MybatisXMLUtils.getTheForeachTagInParents(xmlElement)) {
            if (Objects.equals(DomUtils.getAttributeValue(foreach.getItem()), prefix0)) {
                Optional.ofNullable(foreach.getItem())
                        .map(GenericAttributeValue::getXmlAttributeValue)
                        .ifPresent(resolveResults::add);
            }
            if (Objects.equals(DomUtils.getAttributeValue(foreach.getIndex()), prefix0)) {
                Optional.ofNullable(foreach.getIndex())
                        .map(GenericAttributeValue::getXmlAttributeValue)
                        .ifPresent(resolveResults::add);
            }
        }
        final PsiParameterList parameterList = psiMethod.getParameterList();
        if (parameterList.getParametersCount() > 0) {
            PsiParameter[] psiParameters = parameterList.getParameters();
            Matcher matcher = CompletionUtils.PARAM_PATTERN.matcher(prefix0);
            if (matcher.matches()) {
                int num = Integer.parseInt(matcher.group("num")) - 1;
                if (num <= psiParameters.length) {
                    resolveResults.add(psiParameters[num]);
                }
            }
            if (psiParameters.length == 1) {
                // 如果方法只有一个参数
                PsiParameter firstParameter = psiParameters[0];
                Annotation.Value value = Annotation.PARAM.getValue(firstParameter);
                if (value == null) {
                    if (PsiTypeUtils.isCustomType(firstParameter.getType())) {
                        resolveResults.add(CompletionUtils.getTargetElement(prefix0, firstParameter.getType(), psiField -> psiField, psiMethod1 -> psiMethod1));
                    }
                } else if (value.getValue().equals(prefix0)) {
                    resolveResults.add(firstParameter);
                    // references.addAll(buildReference(element, prefixArr, firstParameter));
                }
            } else if (psiParameters.length > 1) {
                // 如果方法有多个参数
                for (int i = 0; i < psiParameters.length; i++) {
                    PsiParameter psiParameter = psiParameters[i];
                    final Annotation.Value value = Annotation.PARAM.getValue(psiParameter);
                    if (value != null && value.getValue().equals(prefix0)) {
                        resolveResults.add(psiParameter);
                    }
                }
            }
        }
        references.add(new PsiReferenceBase.Poly<>(element, new TextRange(0, prefix0.length()), false) {
            @Override
            public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
                return PsiElementResolveResult.createResults(resolveResults);
            }
        });
        return references;
    }

    public static List<PsiReference> buildReference(@NotNull PsiElement sourceElement, String[] textArr, PsiParameter psiParameter) {
        if (ArrayUtil.isEmpty(textArr) || psiParameter == null) {
            return Collections.emptyList();
        }
        List<PsiReference> references = new ArrayList<>(textArr.length);
        int offsetStart = 0;
        int offsetEnd = textArr[0].length();
        references.add(PsiReferenceBase.createSelfReference(sourceElement, new TextRange(offsetStart, offsetEnd), psiParameter));
        if (!PsiTypeUtils.isCustomType(psiParameter.getType())) {
            return references;
        }
        PsiClass psiClass = ((PsiClassType) psiParameter.getType()).resolve();
        // 从1开始,0就是 psiParameter
        for (int i = 1; i < textArr.length; i++) {
            references.add(new MOgnlDotReference(sourceElement, offsetEnd, psiClass));
            String text = textArr[i];
            PsiMember psiMember = findPsiMember(text, psiClass);
            if (psiMember == null) {
                break;
            }
            offsetStart = offsetStart + 1 + offsetEnd; // textArr[i-1].
            offsetEnd = offsetStart + text.length();
            references.add(new MOgnlIdentifierReference(sourceElement, new TextRange(offsetStart, offsetEnd), text, ((PsiMethod) psiMember)));
            PsiType psiType;
            if (psiMember instanceof PsiField) {
                psiType = ((PsiField) psiMember).getType();
            } else if (psiMember instanceof PsiMethod) {
                psiType = ((PsiMethod) psiMember).getReturnType();
            } else {
                break;
            }
            if (PsiTypeUtils.isCustomType(psiType)) {
                psiClass = ((PsiClassType) psiType).resolve();
            } else {
                break;
            }
        }
        return references;
    }

    public static PsiMember findPsiMember(String text, PsiClass psiClass) {
        if (StringUtil.isEmpty(text) || psiClass == null) {
            return null;
        }
        for (PsiMethod method : psiClass.getMethods()) {
            if (text.equals(method.getName())
                    || PsiMethodUtils.replaceGetPrefix(method).equals(text)) {
                return method;
            }
        }
        // for (PsiField field : psiClass.getFields()) {
        //     if (text.equals(field.getName())) {
        //         return field;
        //     }
        // }
        return findPsiMember(text, psiClass.getSuperClass());
    }

}
