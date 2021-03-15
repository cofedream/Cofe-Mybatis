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

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.GenericAttributeValue;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.CompletionUtils;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.common.utils.PsiJavaUtils;
import tk.cofe.plugin.common.utils.PsiTypeUtils;
import tk.cofe.plugin.mybatis.dom.model.attirubte.NameAttribute;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Foreach;
import tk.cofe.plugin.mybatis.dom.model.include.BindInclude;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.MybatisXMLUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

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
        // 方法参数
        return build(element, originElement, element.getText().split("\\."))
                .toArray(PsiReference.EMPTY_ARRAY);
    }

    private List<PsiReference> build(@NotNull final PsiElement element, final PsiElement originElement, final String[] prefixArr) {
        List<PsiReference> references = new ArrayList<>(prefixArr.length);
        int offsetStart = 0;
        int offsetEnd = prefixArr[0].length();
        // 获取到第一个引用
        final List<? extends PsiElement> firstReferences = getTargetElement(prefixArr[0], originElement);
        references.add(new PsiReferenceBase.Poly<>(element, new TextRange(offsetStart, offsetEnd), false) {
            @Override
            public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
                return PsiElementResolveResult.createResults(firstReferences);
            }
        });
        if (firstReferences.size() == 1 && prefixArr.length > 1) {
            final PsiElement firstReference = firstReferences.get(0);
            PsiType psiType;
            if (firstReference instanceof PsiMember) {
                psiType = PsiJavaUtils.getPsiMemberType((PsiMember) firstReference);
            } else if (firstReference instanceof PsiParameter) {
                psiType = ((PsiParameter) firstReference).getType();
            } else {
                return references;
            }
            for (int i = 1; i < prefixArr.length; i++) {
                // 从第二个开始
                if (!PsiTypeUtils.isCustomType(psiType)) {
                    break;
                }
                final String text = prefixArr[i];
                offsetStart = offsetStart + 1 + offsetEnd; // textArr[i-1].
                offsetEnd = offsetStart + text.length();
                references.add(PsiReferenceBase.createSelfReference(element, new TextRange(offsetStart, offsetEnd), CompletionUtils.getTheGetMethodOrField(text, psiType)));
            }
        }
        return references;
    }

    public List<? extends PsiElement> getTargetElement(String name, PsiElement element) {
        final PsiElement parent = element.getParent();
        if (parent == null) {
            return Collections.emptyList();
        }
        final BindInclude bindInclude = DomUtils.getDomElement(parent, BindInclude.class, false).orElse(null);
        if (bindInclude == null) {
            return Collections.emptyList();
        }
        // 查询元素中包含的Bind
        final List<XmlAttributeValue> bindList = bindInclude.getBinds().stream()
                .map(NameAttribute::getName)
                .filter(bindName -> Objects.equals(name, DomUtils.getAttributeValue(bindName)))
                .map(GenericAttributeValue::getXmlAttributeValue)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(bindList)) {
            return bindList;
        }
        // 如果是Foreach标签则判断item是否符合
        if (bindInclude instanceof Foreach) {
            Foreach foreach = (Foreach) bindInclude;
            final String item = DomUtils.getAttributeValue(foreach.getItem());
            if (Objects.equals(item, name)) {
                return Optional.ofNullable(foreach.getItem())
                        .map(GenericAttributeValue::getXmlAttributeValue)
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList());
            }
        }
        if (bindInclude instanceof ClassElement) {
            // 如果是ClassElement且没有bind标签,则查询对应的方法参数
            ClassElement classElement = (ClassElement) bindInclude;
            final PsiMethod psiMethod = classElement.getIdMethod().orElse(null);
            if (psiMethod == null) {
                return Collections.emptyList();
            }
            final PsiParameterList parameterList = psiMethod.getParameterList();
            if (parameterList.getParametersCount() <= 0) {
                return Collections.emptyList();
            }
            PsiParameter[] psiParameters = parameterList.getParameters();
            Matcher matcher = CompletionUtils.PARAM_PATTERN.matcher(name);
            if (matcher.matches()) {
                int num = Integer.parseInt(matcher.group("num")) - 1;
                if (num <= psiParameters.length) {
                    return Collections.singletonList(psiParameters[num]);
                }
            }
            if (psiParameters.length == 1) {
                // 如果方法只有一个参数
                PsiParameter firstParameter = psiParameters[0];
                Annotation.Value value = Annotation.PARAM.getValue(firstParameter);
                if (value == null) {
                    if (PsiTypeUtils.isCustomType(firstParameter.getType())) {
                        final PsiMember psiMember = CompletionUtils.getTheGetMethodOrField(name, firstParameter.getType());
                        if (psiMember != null) {
                            return Collections.singletonList(psiMember);
                        }
                    }
                } else if (value.getValue().equals(name)) {
                    return Collections.singletonList(firstParameter);
                }
            } else if (psiParameters.length > 1) {
                // 如果方法有多个参数
                for (PsiParameter psiParameter : psiParameters) {
                    final Annotation.Value value = Annotation.PARAM.getValue(psiParameter);
                    if (value != null && value.getValue().equals(name)) {
                        return Collections.singletonList(psiParameter);
                    }
                }
            }
        } else {
            // 如果不是ClassElement元素则继续往上查找
            return getTargetElement(name, parent);
        }
        return Collections.emptyList();
    }

}
