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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mbl.psi.impl.MblPsiUtil;
import tk.cofe.plugin.mybatis.dom.model.attirubte.NameAttribute;
import tk.cofe.plugin.mybatis.dom.model.dynamic.Foreach;
import tk.cofe.plugin.mybatis.dom.model.include.BindInclude;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.CompletionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
        String text = element.getText();
        final String[] splitText = text.split("\\.");
        // bind 标签
        final List<PsiReference> binds = getBinds(element, originElement, text);
        // foreach 标签
        final List<PsiReference> foreach = getForeach(element, originElement, text);
        // 方法参数
        final List<PsiReference> methodParam = getMethodParam(element, originElement, splitText);
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
                .map(bind -> new PsiReferenceBase.Immediate<>(element, new TextRange(0, bind.getTextLength()), bind))
                .collect(Collectors.toList());
    }

    private List<PsiReference> getForeach(@NotNull final PsiElement element, final PsiElement originElement, final String text) {
        return DomUtils.getParents(originElement, XmlTag.class, Foreach.class).stream()
                .map(Foreach::getItem)
                .filter(Objects::nonNull)
                .filter(item -> Objects.equals(text, DomUtils.getAttributeVlaue(item).orElse(null)))
                .map(GenericAttributeValue::getXmlAttributeValue)
                .filter(Objects::nonNull)
                .map(bind -> new PsiReferenceBase.Immediate<>(element, new TextRange(0, bind.getTextLength()), bind))
                .collect(Collectors.toList());
    }

    private List<PsiReference> getMethodParam(@NotNull final PsiElement element, final PsiElement originElement, final String[] splitText) {
        return DomUtils.getDomElement(originElement, ClassElement.class)
                .flatMap(ClassElement::getIdMethod)
                .map(psiMethod -> CompletionUtils.getPrefixElement(splitText, psiMethod.getParameterList().getParameters()))
                .map(resolveTo -> Collections.<PsiReference>singletonList(new PsiReferenceBase.Immediate<>(element, new TextRange(0, element.getTextLength()), resolveTo)))
                .orElse(Collections.emptyList());
    }

}
