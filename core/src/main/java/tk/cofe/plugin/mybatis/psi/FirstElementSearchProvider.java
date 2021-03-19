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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.GenericAttributeValue;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.CompletionUtils;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.common.utils.PsiTypeUtils;
import tk.cofe.plugin.mybatis.dom.model.attirubte.NameAttribute;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Foreach;
import tk.cofe.plugin.mybatis.dom.model.include.BindInclude;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : zhengrf
 * @date : 2021-03-18
 */
public abstract class FirstElementSearchProvider<T> {

    public List<? extends T> getTargetElement(final PsiElement searchElement) {
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
        List<T> res = new ArrayList<>();
        // 查询元素中包含的Bind
        res.addAll(bindInclude.getBinds().stream()
                .map(NameAttribute::getName)
                .map(GenericAttributeValue::getXmlAttributeValue)
                .filter(Objects::nonNull)
                .map(val -> mapper(val.getValue(), val))
                .collect(Collectors.toList()));
        // 如果是Foreach标签则判断item是否符合
        if (bindInclude instanceof Foreach) {
            res.addAll(Optional.ofNullable(((Foreach) bindInclude).getItem())
                    .map(GenericAttributeValue::getXmlAttributeValue)
                    .map(val -> mapper(val.getValue(), val))
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList()));
        }
        if (bindInclude instanceof ClassElement) {
            // 如果是ClassElement且没有bind标签,则查询对应的方法参数
            ((ClassElement) bindInclude).getIdMethod()
                    .map(PsiMethod::getParameterList)
                    .filter(parameterList -> parameterList.getParametersCount() > 0)
                    .map(PsiParameterList::getParameters)
                    .ifPresent(psiParameters -> {
                        for (int i = 0; i < psiParameters.length; i++) {
                            res.add(mapper("param" + (i + 1), psiParameters[i]));
                        }
                        if (psiParameters.length == 1) {
                            // 如果方法只有一个参数
                            PsiParameter firstParameter = psiParameters[0];
                            Annotation.Value value = Annotation.PARAM.getValue(firstParameter);
                            if (value == null) {
                                if (PsiTypeUtils.isCustomType(firstParameter.getType())) {
                                    // 第一个参数使用getMethod/field
                                    for (Map.Entry<String, PsiMember> entry : CompletionUtils.getTheGetMethodAndField(((PsiClassType) firstParameter.getType()).resolve()).entrySet()) {
                                        res.add(mapper(entry.getKey(), entry.getValue()));
                                    }
                                }
                            } else {
                                // todo 需要调整
                                res.add(mapper(value.getValue(), firstParameter));
                            }
                        } else if (psiParameters.length > 1) {
                            // 如果方法有多个参数
                            for (PsiParameter psiParameter : psiParameters) {
                                final Annotation.Value value = Annotation.PARAM.getValue(psiParameter);
                                if (value != null && StringUtil.isNotEmpty(value.getValue())) {
                                    res.add(mapper(value.getValue(), psiParameter));
                                }
                            }
                        }
                    });
        } else {
            // 如果不是ClassElement元素则继续往上查找
            res.addAll(getTargetElement(parent));
        }
        return res;
    }

    @Nonnull
    public abstract T mapper(String name, XmlAttributeValue xmlAttributeValue);

    @Nonnull
    public abstract T mapper(String name, PsiParameter psiParameter);

    @Nonnull
    public abstract T mapper(String name, PsiMember psiMember);
}
