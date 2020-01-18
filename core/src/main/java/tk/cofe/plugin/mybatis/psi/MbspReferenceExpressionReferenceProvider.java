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

package tk.cofe.plugin.mybatis.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.tree.IElementType;
import tk.cofe.plugin.mbsp.MbspTypes;
import tk.cofe.plugin.mbsp.psi.MbspReferenceExpression;
import tk.cofe.plugin.mbsp.psi.MbspReferenceProvider;
import tk.cofe.plugin.mybatis.dom.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.CompletionUtils;
import tk.cofe.plugin.mybatis.util.DomUtils;

import java.util.LinkedList;

/**
 * @author : zhengrf
 * @date : 2019-11-01
 */
public class MbspReferenceExpressionReferenceProvider implements MbspReferenceProvider {
    @Override
    public boolean isSupported(final IElementType elementType) {
        return MbspTypes.REFERENCE_EXPRESSION.equals(elementType);
    }

    @Override
    public PsiReference exec(final PsiElement element, final PsiElement originElement) {
        LinkedList<String> res = new LinkedList<>();
        res.push(element.getText());
        PsiElement prevSibling = element;
        while ((prevSibling = prevSibling.getPrevSibling()) != null) {
            if (prevSibling instanceof MbspReferenceExpression) {
                res.push(prevSibling.getText());
            }
        }
        PsiElement resolveTo = DomUtils.getDomElement(originElement, ClassElement.class)
                .flatMap(ClassElement::getIdMethod)
                .map(psiMethod -> CompletionUtils.getPrefixElement(res.toArray(new String[0]), psiMethod.getParameterList().getParameters()))
                .orElse(null);
        if (resolveTo == null) {
            return null;
        }
        return new PsiReferenceBase.Immediate<>(element, new TextRange(0, element.getTextLength()), resolveTo);
    }

}
