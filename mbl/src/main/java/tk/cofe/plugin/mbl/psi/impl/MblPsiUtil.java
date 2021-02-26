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

package tk.cofe.plugin.mbl.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mbl.MblTypes;
import tk.cofe.plugin.mbl.psi.MblBinaryExpression;
import tk.cofe.plugin.mbl.psi.MblConditionalExpression;
import tk.cofe.plugin.mbl.psi.MblExpression;
import tk.cofe.plugin.mbl.psi.MblLiteralExpression;
import tk.cofe.plugin.mbl.psi.MblParameterList;
import tk.cofe.plugin.mbl.psi.MblParenthesizedExpression;
import tk.cofe.plugin.mbl.psi.MblReferenceExpression;
import tk.cofe.plugin.mbl.psi.MblTokenType;
import tk.cofe.plugin.mbl.psi.MblUnaryExpression;
import tk.cofe.plugin.mbl.psi.MblTokenGroups;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MblPsiUtil {

    @Nullable
    public static PsiType getType(@Nullable MblExpression expression) {
        if (expression == null) {
            return null;
        }

        if (expression instanceof MblLiteralExpression) {
            return resolveLiteralExpressionType((MblLiteralExpression) expression);
        }

        if (expression instanceof MblConditionalExpression) {
            final MblExpression thenExpression = ((MblConditionalExpression) expression).getThen();
            return getType(thenExpression);
        }

        if (expression instanceof MblBinaryExpression) {
            MblExpression leftExpression = ((MblBinaryExpression) expression).getLeft();
            return getType(leftExpression);
        }

        if (expression instanceof MblParenthesizedExpression) {
            MblExpression argument = ((MblParenthesizedExpression) expression).getExpression();
            return getType(argument);
        }

        return null;
    }

    private static PsiType resolveLiteralExpressionType(MblLiteralExpression expression) {
        final ASTNode node = expression.getNode();
        final IElementType type = node.getFirstChildNode().getElementType();

        if (type == MblTypes.STRING_LITERAL) {
            return PsiType.getJavaLangString(expression.getManager(), expression.getResolveScope());
        }
        if (type == MblTypes.CHARACTER_LITERAL) {
            return PsiType.CHAR;
        }
        if (type == MblTypes.INTEGER_LITERAL) {
            return PsiType.INT;
        }
        if (type == MblTypes.DOUBLE_LITERAL) {
            return PsiType.DOUBLE;
        }
        if (type == MblTypes.TRUE_KEYWORD ||
                type == MblTypes.FALSE_KEYWORD) {
            return PsiType.BOOLEAN;
        }
        if (type == MblTypes.NULL_KEYWORD) {
            return PsiType.NULL;
        }
        throw new IllegalArgumentException("could not resolve type for literal " + type + " / " + expression.getText());
    }

    @NotNull
    public static MblTokenType getOperator(MblBinaryExpression expression) {
        final ASTNode node = expression.getNode().findChildByType(MblTokenGroups.OPERATIONS);
        assert node != null : "unknown operation sign: '" + expression.getText() + "'";
        return (MblTokenType) node.getElementType();
    }

    public static int getParameterCount(MblParameterList parameterList) {
        return parameterList.getParametersList().size();
    }

    @NotNull
    public static MblTokenType getUnaryOperator(MblUnaryExpression expression) {
        final ASTNode node = expression.getNode().findChildByType(MblTokenGroups.UNARY_OPS);
        assert node != null : "unknown unary operation sign: '" + expression.getText() + "'";
        return (MblTokenType) node.getElementType();
    }

    @NotNull
    public static PsiReference[] getReferences(MblReferenceExpression element) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(element);
    }

    @Nullable
    public static PsiElement getOriginElement(final PsiElement element) {
        return InjectedLanguageManager.getInstance(element.getProject()).getInjectionHost(element);
    }

    @Nullable
    static Object getConstantValue(MblLiteralExpression expression) {
        final ASTNode node = expression.getNode();
        final IElementType type = node.getFirstChildNode().getElementType();

        final String text = expression.getText();
        if (type == MblTypes.STRING_LITERAL) {
            return StringUtil.unquoteString(text);
        }
        if (type == MblTypes.CHARACTER_LITERAL) {
            return StringUtil.unquoteString(text);
        }
        if (type == MblTypes.INTEGER_LITERAL) {
            return Integer.parseInt(text);
        }
        if (type == MblTypes.DOUBLE_LITERAL) {
            return Double.parseDouble(text);
        }
        if (type == MblTypes.TRUE_KEYWORD ||
                type == MblTypes.FALSE_KEYWORD) {
            return Boolean.valueOf(text);
        }
        if (type == MblTypes.NULL_KEYWORD) {
            return null;
        }

        throw new IllegalArgumentException("could not resolve constant value for literal " + type + " / " + text);
    }

}
