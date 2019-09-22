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

package tk.cofe.plugin.mybatis.dom.psi.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.mybatis.constants.Empty;
import tk.cofe.plugin.mybatis.dom.description.model.tag.ClassElement;
import tk.cofe.plugin.mybatis.util.DomUtils;
import tk.cofe.plugin.mybatis.util.MybatisUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Xml Attribute 引用
 *
 * @author : zhengrf
 * @date : 2019-01-05
 */
public class IdAttributeReference extends PsiReferenceBase.Poly<PsiElement> {
    public IdAttributeReference(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> result = new ArrayList<>();
        XmlTag tag = PsiTreeUtil.getParentOfType(myElement, XmlTag.class);
        if (MybatisUtils.isBaseStatement(tag)) {
            ClassElement classElement = (ClassElement) DomUtils.getDomElement(tag);
            if (classElement == null) {
                return Empty.Array.RESOLVE_RESULT;
            }
            classElement.getIdMethod().ifPresent(psiMethod -> result.add(new PsiElementResolveResult(psiMethod)));
        }
        if (result.isEmpty()) {
            result.add(new PsiElementResolveResult(myElement));
        }
        return result.toArray(Empty.Array.RESOLVE_RESULT);
    }

}
