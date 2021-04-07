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

package tk.cofe.plugin.mybatis.search;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlText;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.mbel.psi.MbELReferenceExpression;
import tk.cofe.plugin.mybatis.service.MapperService;

/**
 * @author : zhengrf
 * @date : 2021-04-07
 */
public class MethodParameterReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {
    protected MethodParameterReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(ReferencesSearch.@NotNull SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        final PsiElement elementToSearch = queryParameters.getElementToSearch();
        if (!(elementToSearch instanceof PsiParameter)) {
            return;
        }
        PsiParameter psiParameter = (PsiParameter) elementToSearch;
        final PsiMethod psiMethod = PsiTreeUtil.getParentOfType(psiParameter, PsiMethod.class);
        if (psiMethod == null) {
            return;
        }
        final PsiClass psiClass = PsiTreeUtil.getParentOfType(psiMethod, PsiClass.class);
        if (psiClass == null) {
            return;
        }
        final String parameterName = Annotation.PARAM.getValue(psiParameter, psiParameter::getName).getValue();
        MapperService.getInstance(psiClass.getProject()).findStatement(psiMethod).map(DomElement::getXmlTag)
                .ifPresent(xmlTag -> {
                    final InjectedLanguageManager languageManager = InjectedLanguageManager.getInstance(xmlTag.getProject());
                    for (XmlText xmlText : PsiTreeUtil.getChildrenOfTypeAsList(xmlTag, XmlText.class)) {
                        final String text = xmlText.getText();
                        int lbrace;
                        int rbrace = 0;
                        while ((lbrace = text.indexOf("#{", rbrace)) != -1 && (rbrace = text.indexOf('}', lbrace)) != -1) {
                            final int paramIndex = text.indexOf(parameterName, lbrace);
                            if (paramIndex != -1 && paramIndex < rbrace) {
                                final int offset = xmlText.getTextOffset() + paramIndex;
                                final PsiElement injectedElementAt = languageManager.findInjectedElementAt(xmlTag.getContainingFile(), offset);
                                final MbELReferenceExpression parent = PsiTreeUtil.getParentOfType(injectedElementAt, MbELReferenceExpression.class);
                                if (parent != null && parent.getTextLength() == injectedElementAt.getTextLength()) {
                                    consumer.process(new PsiReferenceBase<>(injectedElementAt, TextRange.allOf(parameterName)) {
                                        @Override
                                        public @NotNull PsiElement resolve() {
                                            return psiParameter;
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
    }

}
