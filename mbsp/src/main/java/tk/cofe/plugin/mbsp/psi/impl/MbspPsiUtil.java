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

package tk.cofe.plugin.mbsp.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mbsp.MbspTypes;
import tk.cofe.plugin.mbsp.psi.MbspBinaryExpression;
import tk.cofe.plugin.mbsp.psi.MbspConditionalExpression;
import tk.cofe.plugin.mbsp.psi.MbspExpression;
import tk.cofe.plugin.mbsp.psi.MbspLiteralExpression;
import tk.cofe.plugin.mbsp.psi.MbspParameterList;
import tk.cofe.plugin.mbsp.psi.MbspParenthesizedExpression;
import tk.cofe.plugin.mbsp.psi.MbspReferenceExpression;
import tk.cofe.plugin.mbsp.psi.MbspTokenGroups;
import tk.cofe.plugin.mbsp.psi.MbspTokenType;
import tk.cofe.plugin.mbsp.psi.MbspUnaryExpression;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MbspPsiUtil {

    @Nullable
    public static PsiType getType(@Nullable MbspExpression expression) {
        if (expression == null) {
            return null;
        }

        if (expression instanceof MbspLiteralExpression) {
            return resolveLiteralExpressionType((MbspLiteralExpression) expression);
        }

        if (expression instanceof MbspConditionalExpression) {
            final MbspExpression thenExpression = ((MbspConditionalExpression) expression).getThen();
            return getType(thenExpression);
        }

        if (expression instanceof MbspBinaryExpression) {
            MbspExpression leftExpression = ((MbspBinaryExpression) expression).getLeft();
            return getType(leftExpression);
        }

        if (expression instanceof MbspParenthesizedExpression) {
            MbspExpression argument = ((MbspParenthesizedExpression) expression).getExpression();
            return getType(argument);
        }

        return null;
    }

    private static PsiType resolveLiteralExpressionType(MbspLiteralExpression expression) {
        final ASTNode node = expression.getNode();
        final IElementType type = node.getFirstChildNode().getElementType();

        if (type == MbspTypes.STRING_LITERAL) {
            return PsiType.getJavaLangString(expression.getManager(), expression.getResolveScope());
        }
        if (type == MbspTypes.CHARACTER_LITERAL) {
            return PsiType.CHAR;
        }
        if (type == MbspTypes.INTEGER_LITERAL) {
            return PsiType.INT;
        }
        if (type == MbspTypes.DOUBLE_LITERAL) {
            return PsiType.DOUBLE;
        }
        if (type == MbspTypes.TRUE_KEYWORD ||
                type == MbspTypes.FALSE_KEYWORD) {
            return PsiType.BOOLEAN;
        }
        if (type == MbspTypes.NULL_KEYWORD) {
            return PsiType.NULL;
        }
        throw new IllegalArgumentException("could not resolve type for literal " + type + " / " + expression.getText());
    }

    @NotNull
    public static MbspTokenType getOperator(MbspBinaryExpression expression) {
        final ASTNode node = expression.getNode().findChildByType(MbspTokenGroups.OPERATIONS);
        assert node != null : "unknown operation sign: '" + expression.getText() + "'";
        return (MbspTokenType) node.getElementType();
    }

    public static int getParameterCount(MbspParameterList parameterList) {
        return parameterList.getParametersList().size();
    }

    @NotNull
    public static MbspTokenType getUnaryOperator(MbspUnaryExpression expression) {
        final ASTNode node = expression.getNode().findChildByType(MbspTokenGroups.UNARY_OPS);
        assert node != null : "unknown unary operation sign: '" + expression.getText() + "'";
        return (MbspTokenType) node.getElementType();
    }

    @NotNull
    public static PsiReference[] getReferences(MbspReferenceExpression element) {
        return ReferenceProvidersRegistry.getReferencesFromProviders(element);
    }

    @Nullable
    public static PsiElement getOriginElement(final PsiElement element) {
        return InjectedLanguageManager.getInstance(element.getProject()).getInjectionHost(element);
    }

    @Nullable
    static Object getConstantValue(MbspLiteralExpression expression) {
        final ASTNode node = expression.getNode();
        final IElementType type = node.getFirstChildNode().getElementType();

        final String text = expression.getText();
        if (type == MbspTypes.STRING_LITERAL) {
            return StringUtil.unquoteString(text);
        }
        if (type == MbspTypes.CHARACTER_LITERAL) {
            return StringUtil.unquoteString(text);
        }
        if (type == MbspTypes.INTEGER_LITERAL) {
            return Integer.parseInt(text);
        }
        if (type == MbspTypes.DOUBLE_LITERAL) {
            return Double.parseDouble(text);
        }
        if (type == MbspTypes.TRUE_KEYWORD ||
                type == MbspTypes.FALSE_KEYWORD) {
            return Boolean.valueOf(text);
        }
        if (type == MbspTypes.NULL_KEYWORD) {
            return null;
        }

        throw new IllegalArgumentException("could not resolve constant value for literal " + type + " / " + text);
    }

}
