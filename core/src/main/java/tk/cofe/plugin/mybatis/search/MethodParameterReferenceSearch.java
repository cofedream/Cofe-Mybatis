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
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import tk.cofe.plugin.common.annotation.Annotation;
import tk.cofe.plugin.common.utils.DomUtils;
import tk.cofe.plugin.mbel.psi.MbELReferenceExpression;
import tk.cofe.plugin.mognl.psi.MOgnlExpression;
import tk.cofe.plugin.mognl.psi.MOgnlReferenceExpression;
import tk.cofe.plugin.mybatis.dom.model.attirubte.TestAttribute;
import tk.cofe.plugin.mybatis.dom.model.tag.dynamic.Foreach;
import tk.cofe.plugin.mybatis.service.MapperService;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

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
            final Process process = new Process(element -> consumer.process(createRef(psiParameter, needRename, element, TextRange.allOf(parameterName))), parameterName, languageManager, xmlFile);
            for (XmlText xmlText : PsiTreeUtil.findChildrenOfType(xmlTag, XmlText.class)) {
                process.init(xmlText).process("#{", MbELReferenceExpression.class).process("${", MbELReferenceExpression.class);
            }
            for (XmlTag subTag : PsiTreeUtil.findChildrenOfType(xmlTag, XmlTag.class)) {
                final DomElement domElement = DomUtils.getDomElement(subTag);
                for (MOgnlReferenceExpression mOgnlReferenceExpression : Optional.ofNullable(domElement).filter(TestAttribute.class::isInstance)
                        .map(TestAttribute.class::cast).map(TestAttribute::getTest).map(GenericAttributeValue::getXmlAttributeValue)
                        .map(xmlAttributeValue -> languageManager.findInjectedElementAt(xmlFile, xmlAttributeValue.getTextOffset()))
                        .map(element -> PsiTreeUtil.getTopmostParentOfType(element, MOgnlExpression.class))
                        .map(mOgnlExpression -> PsiTreeUtil.findChildrenOfType(mOgnlExpression, MOgnlReferenceExpression.class))
                        .orElse(Collections.emptyList())) {
                    if (mOgnlReferenceExpression.getText().indexOf(parameterName) == 0) {
                        consumer.process(createRef(psiParameter, needRename, mOgnlReferenceExpression, TextRange.allOf(parameterName)));
                    }
                }
                Optional.ofNullable(domElement).filter(Foreach.class::isInstance)
                        .map(Foreach.class::cast).map(Foreach::getCollection).map(GenericAttributeValue::getXmlAttributeValue)
                        .filter(xmlAttributeValue -> xmlAttributeValue.getValue().indexOf(parameterName) == 0)
                        .ifPresent(xmlAttributeValue -> consumer.process(createRef(psiParameter, needRename, xmlAttributeValue, TextRange.from(1, parameterName.length()))));
            }
        });
    }

    private static final class Process {
        private final Consumer<PsiElement> elementConsumer;
        private final String parameterName;
        private final InjectedLanguageManager languageManager;
        private final PsiFile xmlFile;

        public Process(Consumer<PsiElement> elementConsumer, String parameterName, InjectedLanguageManager languageManager, PsiFile xmlFile) {
            this.elementConsumer = elementConsumer;
            this.parameterName = parameterName;
            this.languageManager = languageManager;
            this.xmlFile = xmlFile;
        }

        public static class Init {
            private final Process process;
            private final String text;
            private final int xmlTextOffset;

            private Init(Process process, XmlText xmlText) {
                this.process = process;
                this.text = xmlText.getText();
                this.xmlTextOffset = xmlText.getTextOffset();
            }

            public Init process(final String prefix, final Class<? extends PsiElement> parentClass) {
                int lbrace;
                int rbrace = 0;
                while ((lbrace = text.indexOf(prefix, rbrace)) != -1 && (rbrace = text.indexOf("}", lbrace)) != -1) {
                    final int paramIndex = text.indexOf(process.parameterName, lbrace);
                    // #{/${ 占位
                    if (paramIndex != -1 && paramIndex == lbrace + 2) {
                        Optional.ofNullable(process.languageManager.findInjectedElementAt(process.xmlFile, xmlTextOffset + paramIndex))
                                .map(element -> PsiTreeUtil.getParentOfType(element, parentClass))
                                .ifPresent(process.elementConsumer);
                    }
                }
                return this;
            }
        }

        public Init init(XmlText xmlText) {
            return new Init(this, xmlText);
        }
    }

    @NotNull
    private PsiReferenceBase<PsiElement> createRef(PsiElement myResolveTo, boolean needRename, PsiElement parent, @NotNull final TextRange textRange) {
        return new PsiReferenceBase<>(parent, textRange) {
            @Override
            public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
                if (!needRename) {
                    return myElement;
                }
                return super.handleElementRename(newElementName);
            }

            @Override
            public @NotNull PsiElement resolve() {
                return myResolveTo;
            }
        };
    }

}
