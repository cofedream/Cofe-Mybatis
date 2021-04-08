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
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.mbel.psi.MbELReferenceExpression;
import tk.cofe.plugin.mognl.psi.MOgnlReferenceExpression;
import tk.cofe.plugin.mybatis.service.MapperService;

import java.util.Objects;

/**
 * Mapper接口方法参数使用查找
 *
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
        final Annotation.Value value = Annotation.PARAM.getValue(psiParameter);
        if (value == null) {
            return;
        }
        final PsiMethod psiMethod = PsiTreeUtil.getParentOfType(psiParameter, PsiMethod.class);
        if (psiMethod == null) {
            return;
        }
        final PsiClass psiClass = PsiTreeUtil.getParentOfType(psiMethod, PsiClass.class);
        if (psiClass == null) {
            return;
        }
        MapperService.getInstance(psiClass.getProject()).findStatement(psiMethod).map(DomElement::getXmlTag).ifPresent(xmlTag -> {
            final PsiFile xmlFile = xmlTag.getContainingFile();
            // 注解值与方法名不一致则不进行重命名
            final boolean needRename = Objects.equals(value.getValue(), psiParameter.getName());
            final String parameterName = value.getValue();
            final InjectedLanguageManager languageManager = InjectedLanguageManager.getInstance(xmlTag.getProject());
            for (XmlText xmlText : PsiTreeUtil.getChildrenOfTypeAsList(xmlTag, XmlText.class)) {
                final String text = xmlText.getText();
                final int xmlTextOffset = xmlText.getTextOffset();
                processMbEL(consumer, psiParameter, needRename, parameterName, languageManager, text, xmlFile, xmlTextOffset);
                processMOgnl(consumer, psiParameter, needRename, parameterName, languageManager, text, xmlFile, xmlTextOffset);
            }
        });
    }

    private void processMbEL(@NotNull Processor<? super PsiReference> consumer, PsiParameter psiParameter, boolean needRename, String parameterName, InjectedLanguageManager languageManager, String text, final PsiFile xmlFile, final int xmlTextOffset) {
        int lbrace;
        int rbrace = 0;
        while ((lbrace = text.indexOf("#{", rbrace)) != -1 && (rbrace = text.indexOf("}", lbrace)) != -1) {
            final int paramIndex = text.indexOf(parameterName, lbrace);
            if (paramIndex != -1 && paramIndex == lbrace + 2) {
                final int offset = xmlTextOffset + paramIndex;
                final PsiElement injectedElementAt = languageManager.findInjectedElementAt(xmlFile, offset);
                final MbELReferenceExpression parent = PsiTreeUtil.getParentOfType(injectedElementAt, MbELReferenceExpression.class);
                if (parent != null) {
                    consumer.process(new PsiReferenceBase<>(parent, TextRange.allOf(parameterName)) {
                        @Override
                        public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
                            if (!needRename) {
                                return myElement;
                            }
                            return super.handleElementRename(newElementName);
                        }

                        @Override
                        public @NotNull PsiElement resolve() {
                            return psiParameter;
                        }
                    });
                }
            }
        }
    }

    private void processMOgnl(@NotNull Processor<? super PsiReference> consumer, PsiParameter psiParameter, boolean needRename, String parameterName, InjectedLanguageManager languageManager, String text, final PsiFile xmlFile, final int xmlTextOffset) {
        int lbrace;
        int rbrace = 0;
        while ((lbrace = text.indexOf("${", rbrace)) != -1 && (rbrace = text.indexOf('}', lbrace)) != -1) {
            final int paramIndex = text.indexOf(parameterName, lbrace);
            if (paramIndex != -1 && paramIndex == lbrace + 2) {
                final int offset = xmlTextOffset + paramIndex;
                final PsiElement injectedElementAt = languageManager.findInjectedElementAt(xmlFile, offset);
                final MOgnlReferenceExpression parent = PsiTreeUtil.getParentOfType(injectedElementAt, MOgnlReferenceExpression.class);
                if (parent != null) {
                    consumer.process(new PsiReferenceBase<>(parent, TextRange.allOf(parameterName)) {
                        @Override
                        public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
                            if (!needRename) {
                                return myElement;
                            }
                            return super.handleElementRename(newElementName);
                        }

                        @Override
                        public @NotNull PsiElement resolve() {
                            return psiParameter;
                        }
                    });
                }
            }
        }
    }

}
