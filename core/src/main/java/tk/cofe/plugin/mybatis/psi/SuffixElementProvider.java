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

package tk.cofe.plugin.mybatis.psi;

import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.GenericAttributeValue;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.CompletionUtils;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.common.utils.PsiJavaUtils;
import tk.cofe.plugin.common.utils.PsiTypeUtils;
import tk.cofe.plugin.mybatis.dom.model.attirubte.NameAttribute;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Foreach;
import tk.cofe.plugin.mybatis.dom.model.include.BindInclude;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2021-03-18
 */
public abstract class SuffixElementProvider<T> {

    public List<? extends T> getTargetElement(String name, PsiElement injectElement, final PsiElement searchElement) {
        if (searchElement == null) {
            return Collections.emptyList();
        }
        final PsiElement parent = searchElement.getParent();
        if (parent == null) {
            return Collections.emptyList();
        }
        final BindInclude bindInclude = DomUtils.getDomElement(parent, BindInclude.class).orElse(null);
        if (bindInclude == null) {
            return Collections.emptyList();
        }
        // 查询元素中包含的Bind
        final List<T> bindList = bindInclude.getBinds().stream()
                .map(NameAttribute::getName)
                .filter(bindName -> Objects.equals(name, DomUtils.getAttributeValue(bindName)))
                .map(GenericAttributeValue::getXmlAttributeValue)
                .map(resolve -> mapper(injectElement, resolve))
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
                        .map(resolve -> mapper(injectElement, resolve))
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
                    return Collections.singletonList(mapper(injectElement, psiParameters[num]));
                }
            }
            if (psiParameters.length == 1) {
                // 如果方法只有一个参数
                PsiParameter firstParameter = psiParameters[0];
                Annotation.Value value = Annotation.PARAM.getValue(firstParameter);
                if (value == null) {
                    if (PsiTypeUtils.isCustomType(firstParameter.getType())) {
                        // 第一个参数使用getMethod/field
                        final PsiMember psiMember = CompletionUtils.getTheGetMethodOrField(name, firstParameter.getType());
                        if (psiMember != null) {
                            return Collections.singletonList(mapper(injectElement, name, psiMember, PsiJavaUtils.getPsiMemberType(psiMember)));
                        }
                    }
                } else if (value.getValue().equals(name)) {
                    return Collections.singletonList(mapper(injectElement, name, firstParameter, firstParameter.getType()));
                }
            } else if (psiParameters.length > 1) {
                // 如果方法有多个参数
                for (PsiParameter psiParameter : psiParameters) {
                    final Annotation.Value value = Annotation.PARAM.getValue(psiParameter);
                    if (value != null && value.getValue().equals(name)) {
                        return Collections.singletonList(mapper(injectElement, name, psiParameter, psiParameter.getType()));
                    }
                }
            }
        } else {
            // 如果不是ClassElement元素则继续往上查找
            return getTargetElement(name, injectElement, parent);
        }
        return Collections.emptyList();
    }

    public abstract T mapper(PsiElement injectElement, XmlAttributeValue xmlAttributeValue);

    public abstract T mapper(PsiElement injectElement, PsiParameter psiParameter);

    public abstract T mapper(PsiElement injectElement, final String name, PsiElement targetElement, @Nullable final PsiType type);
}
