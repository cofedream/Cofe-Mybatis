/*
 * Copyright (C) 2019 cofe
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mybatis.completion;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.completion.AutoCompletionContext;
import com.intellij.codeInsight.completion.AutoCompletionDecision;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassObjectAccessExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author : zhengrf
 * @date : 2019-08-18
 */
public class TkMapperCompletionContributor extends CompletionContributor {
    public TkMapperCompletionContributor() {
        super();
    }

    @Override
    public void fillCompletionVariants(@NotNull final CompletionParameters parameters, @NotNull final CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        PsiElement position = parameters.getPosition();
        if (!(position instanceof PsiJavaToken)) {
            return;
        }

        //PsiTreeUtil.getParentOfType(position, Psimet);
        PsiMethodCallExpression callExpression = PsiTreeUtil.getParentOfType(position, PsiMethodCallExpression.class);
        if (callExpression != null) {
            PsiReferenceExpression methodExpression = callExpression.getMethodExpression();
            PsiExpression qualifierExpression = methodExpression.getQualifierExpression();
            if (qualifierExpression != null) {
                PsiReference reference = qualifierExpression.getReference();
                if (reference != null) {
                    PsiElement resolve = reference.resolve();
                    if (resolve instanceof PsiLocalVariable) {
                        PsiLocalVariable psiLocalVariable = (PsiLocalVariable) resolve;
                        PsiMethodCallExpression childOfAnyType = PsiTreeUtil.getChildOfAnyType(psiLocalVariable, PsiMethodCallExpression.class);
                        if (childOfAnyType != null) {
                            PsiExpressionList argumentList = childOfAnyType.getArgumentList();
                            PsiExpression[] expressions = argumentList.getExpressions();
                            for (PsiExpression expression : expressions) {
                                if (expression instanceof PsiClassObjectAccessExpression) {
                                    PsiType type = expression.getType();
                                    PsiTypeElement operand = ((PsiClassObjectAccessExpression) expression).getOperand();
                                    PsiType type1 = operand.getType();
                                    if (type1 instanceof PsiClassReferenceType) {
                                        PsiClass resolve1 = ((PsiClassReferenceType) type1).resolve();
                                        if (resolve1 != null) {
                                            for (PsiField psiField : resolve1.getAllFields()) {
                                                PsiAnnotation[] annotations = psiField.getAnnotations();
                                                if (psiField.hasAnnotation("javax.persistence.Column")) {
                                                    PsiAnnotation annotation = psiField.getAnnotation("javax.persistence.Column");
                                                    if (annotation != null) {
                                                        String name = AnnotationUtil.getStringAttributeValue(annotation, "name");
                                                        if (StringUtil.isNotEmpty(name)) {
                                                            String lookupString = "\"" + name + "\"";
                                                            result.addElement(PrioritizedLookupElement.withPriority(LookupElementBuilder.create(lookupString, lookupString), 1));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    System.out.println();
                                }
                            }
                        }
                    }
                }
            }

            PsiElement qualifier = methodExpression.getQualifier();
        }
        super.fillCompletionVariants(parameters, result);
    }

    @Override
    public void beforeCompletion(@NotNull final CompletionInitializationContext context) {
        super.beforeCompletion(context);
    }

    @Nullable
    @Override
    public String handleEmptyLookup(@NotNull final CompletionParameters parameters, final Editor editor) {
        return super.handleEmptyLookup(parameters, editor);
    }

    @Nullable
    @Override
    public AutoCompletionDecision handleAutoCompletionPossibility(@NotNull final AutoCompletionContext context) {
        return super.handleAutoCompletionPossibility(context);
    }

    @Override
    public void duringCompletion(@NotNull final CompletionInitializationContext context) {
        super.duringCompletion(context);
    }
}
