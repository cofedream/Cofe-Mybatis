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
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.*;
import tk.cofe.plugin.mbl.psi.MblDotReference;
import tk.cofe.plugin.mbl.psi.MblIdentifierReference;
import tk.cofe.plugin.mbl.psi.impl.MblPsiUtil;
import tk.cofe.plugin.mybatis.dom.model.attirubte.NameAttribute;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Foreach;
import tk.cofe.plugin.mybatis.dom.model.include.BindInclude;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2020-01-19
 */
public class MblReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull final ProcessingContext context) {
        if (element.getTextLength() <= 0) {
            return PsiReference.EMPTY_ARRAY;
        }
        final PsiElement originElement = MblPsiUtil.getOriginElement(element);
        if (originElement == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        String text = element.getText().replace(CompletionUtilCore.DUMMY_IDENTIFIER, "");
        final String[] splitTextArr = text.split("\\.");
        // bind 标签
        final List<PsiReference> binds = getBinds(element, originElement, text);
        // foreach 标签
        final List<PsiReference> foreach = getForeach(element, originElement, text);
        // 方法参数
        final List<PsiReference> methodParam = getMethodParam(element, originElement, splitTextArr);
        List<PsiReference> res = new ArrayList<>(binds.size() + methodParam.size() + foreach.size());
        res.addAll(binds);
        res.addAll(foreach);
        res.addAll(methodParam);
        return res.toArray(PsiReference.EMPTY_ARRAY);
    }

    private List<PsiReference> getBinds(@NotNull final PsiElement element, final PsiElement originElement, final String text) {
        return DomUtils.getParents(originElement, XmlTag.class, BindInclude.class).stream()
                .flatMap(info -> info.getBinds().stream())
                .map(NameAttribute::getName)
                .filter(bind -> Objects.equals(text, DomUtils.getAttributeVlaue(bind).orElse(null)))
                .map(GenericAttributeValue::getXmlAttributeValue)
                .filter(Objects::nonNull)
                .map(bind -> PsiReferenceBase.createSelfReference(element, new TextRange(0, bind.getTextLength()), bind))
                .collect(Collectors.toList());
    }

    private List<PsiReference> getForeach(@NotNull final PsiElement element, final PsiElement originElement, final String text) {
        return DomUtils.getParents(originElement, XmlTag.class, Foreach.class).stream()
                .map(Foreach::getItem)
                .filter(Objects::nonNull)
                .filter(item -> Objects.equals(text, DomUtils.getAttributeVlaue(item).orElse(null)))
                .map(GenericAttributeValue::getXmlAttributeValue)
                .filter(Objects::nonNull)
                .map(bind -> PsiReferenceBase.createSelfReference(element, new TextRange(0, bind.getTextLength()), bind))
                .collect(Collectors.toList());
    }

    private List<PsiReference> getMethodParam(@NotNull final PsiElement element, final PsiElement originElement, final String[] prefixArr) {
        ClassElement classElement = DomUtils.getDomElement(originElement, ClassElement.class).orElse(null);
        if (classElement == null) {
            return Collections.emptyList();
        }
        PsiMethod psiMethod = classElement.getIdMethod().orElse(null);
        if (psiMethod == null) {
            return Collections.emptyList();
        }
        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        if (psiParameters.length == 0) {
            return Collections.emptyList();
        }
        String prefix0 = prefixArr[0];
        Matcher matcher = CompletionUtils.PARAM_PATTERN.matcher(prefix0);
        if (matcher.matches()) {
            int num = Integer.parseInt(matcher.group("num")) - 1;
            if (num > psiParameters.length) {
                return Collections.emptyList();
            }
            PsiParameter psiParameter = psiParameters[num];
        }
        List<PsiReference> references = new ArrayList<>(psiParameters.length);
        if (psiParameters.length == 1) {
            // 如果方法只有一个参数
            PsiParameter firstParameter = psiParameters[0];
            Annotation.Value value = Annotation.PARAM.getValue(firstParameter);
            if (value == null) {
                if (PsiTypeUtils.isCustomType(firstParameter.getType())) {
                    PsiMember targetElement = CompletionUtils.getTargetElement(prefix0, firstParameter.getType(), psiField -> psiField, psiMethod1 -> psiMethod1);
                    references.add(PsiReferenceBase.createSelfReference(element, new TextRange(0, element.getTextLength()), targetElement));
                }
            } else if (value.getValue().equals(prefix0)) {
                references.addAll(buildReference(element, prefixArr, firstParameter));
            }
        } else {
            // 如果方法有多个参数
            // if (psiParameters.length > 1) {
            //     for (int i = 0; i < psiParameters.length; i++) {
            //         PsiParameter psiParameter = psiParameters[i];
            //         Annotation.PARAM.getValue(firstParameter);
            //     }
            // }
        }
        return references;
        // return DomUtils.getDomElement(originElement, ClassElement.class)
        //         .flatMap(ClassElement::getIdMethod)
        //         .map(psiMethod -> CompletionUtils.getPrefixElement(prefixArr, psiMethod.getParameterList().getParameters()))
        //         .map(resolveTo -> Collections.<PsiReference>singletonList(new PsiReferenceBase.Immediate<>(element, new TextRange(0, element.getTextLength()), resolveTo)))
        //         .orElse(Collections.emptyList());
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
            references.add(new MblDotReference(sourceElement, offsetEnd, psiClass));
            String text = textArr[i];
            PsiMember psiMember = findPsiMember(text, psiClass);
            if (psiMember == null) {
                break;
            }
            offsetStart = offsetStart + 1 + offsetEnd; // textArr[i-1].
            offsetEnd = offsetStart + text.length();
            references.add(new MblIdentifierReference(sourceElement, new TextRange(offsetStart, offsetEnd),text, ((PsiMethod) psiMember)));
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
