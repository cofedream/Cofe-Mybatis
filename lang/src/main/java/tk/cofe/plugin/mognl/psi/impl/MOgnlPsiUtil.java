/*
 * Copyright (C) 2019-2023 cofe
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

package tk.cofe.plugin.mognl.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypes;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mognl.MOgnlTypes;
import tk.cofe.plugin.mognl.psi.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MOgnlPsiUtil {

    @Nullable
    public static PsiType getType(@Nullable MOgnlExpression expression) {
        if (expression == null) {
            return null;
        }

        if (expression instanceof MOgnlLiteralExpression) {
            return resolveLiteralExpressionType((MOgnlLiteralExpression) expression);
        }

        if (expression instanceof MOgnlConditionalExpression) {
            final MOgnlExpression thenExpression = ((MOgnlConditionalExpression) expression).getThen();
            return getType(thenExpression);
        }

        if (expression instanceof MOgnlBinaryExpression) {
            MOgnlExpression leftExpression = ((MOgnlBinaryExpression) expression).getLeft();
            return getType(leftExpression);
        }

        if (expression instanceof MOgnlParenthesizedExpression) {
            MOgnlExpression argument = ((MOgnlParenthesizedExpression) expression).getExpression();
            return getType(argument);
        }

        return null;
    }

    private static PsiType resolveLiteralExpressionType(MOgnlLiteralExpression expression) {
        final ASTNode node = expression.getNode();
        final IElementType type = node.getFirstChildNode().getElementType();

        if (type == MOgnlTypes.STRING_LITERAL) {
            return PsiType.getJavaLangString(expression.getManager(), expression.getResolveScope());
        }
        if (type == MOgnlTypes.CHARACTER_LITERAL) {
            return PsiTypes.charType();
        }
        if (type == MOgnlTypes.INTEGER_LITERAL) {
            return PsiTypes.intType();
        }
        if (type == MOgnlTypes.DOUBLE_LITERAL) {
            return PsiTypes.doubleType();
        }
        if (type == MOgnlTypes.TRUE_KEYWORD ||
                type == MOgnlTypes.FALSE_KEYWORD) {
            return PsiTypes.booleanType();
        }
        if (type == MOgnlTypes.NULL_KEYWORD) {
            return PsiTypes.nullType();
        }
        throw new IllegalArgumentException("could not resolve type for literal " + type + " / " + expression.getText());
    }

    @NotNull
    public static MOgnlTokenType getOperator(MOgnlBinaryExpression expression) {
        final ASTNode node = expression.getNode().findChildByType(MOgnlTokenGroups.OPERATIONS);
        assert node != null : "unknown operation sign: '" + expression.getText() + "'";
        return (MOgnlTokenType) node.getElementType();
    }

    public static int getParameterCount(MOgnlParameterList parameterList) {
        return parameterList.getParametersList().size();
    }

    @NotNull
    public static MOgnlTokenType getUnaryOperator(MOgnlUnaryExpression expression) {
        final ASTNode node = expression.getNode().findChildByType(MOgnlTokenGroups.UNARY_OPS);
        assert node != null : "unknown unary operation sign: '" + expression.getText() + "'";
        return (MOgnlTokenType) node.getElementType();
    }

    @NotNull
    public static PsiReference[] getReferences(MOgnlReferenceExpression element) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(element);
    }

    @NotNull
    public static PsiElement[] getChildren(MOgnlReferenceExpression element) {
        PsiElement psiChild = element.getFirstChild();
        if (psiChild == null) return PsiElement.EMPTY_ARRAY;

        List<PsiElement> result = new ArrayList<>();
        while (psiChild != null) {
            if (psiChild.getNode() instanceof MOgnlTokenImpl) {
                result.add(psiChild);
            }
            psiChild = psiChild.getNextSibling();
        }
        return PsiUtilCore.toPsiElementArray(result);
    }

    @Nullable
    static Object getConstantValue(MOgnlLiteralExpression expression) {
        final ASTNode node = expression.getNode();
        final IElementType type = node.getFirstChildNode().getElementType();

        final String text = expression.getText();
        if (type == MOgnlTypes.STRING_LITERAL) {
            return StringUtil.unquoteString(text);
        }
        if (type == MOgnlTypes.CHARACTER_LITERAL) {
            return StringUtil.unquoteString(text);
        }
        if (type == MOgnlTypes.INTEGER_LITERAL) {
            return Integer.parseInt(text);
        }
        if (type == MOgnlTypes.DOUBLE_LITERAL) {
            return Double.parseDouble(text);
        }
        if (type == MOgnlTypes.TRUE_KEYWORD ||
                type == MOgnlTypes.FALSE_KEYWORD) {
            return Boolean.valueOf(text);
        }
        if (type == MOgnlTypes.NULL_KEYWORD) {
            return null;
        }

        throw new IllegalArgumentException("could not resolve constant value for literal " + type + " / " + text);
    }

}
