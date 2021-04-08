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
package tk.cofe.plugin.mognl;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import tk.cofe.plugin.mognl.psi.MOgnlElementType;
import tk.cofe.plugin.mognl.psi.MOgnlTokenType;
import tk.cofe.plugin.mognl.psi.impl.*;

public interface MOgnlTypes {

  IElementType BINARY_EXPRESSION = new MOgnlElementType("BINARY_EXPRESSION");
  IElementType CONDITIONAL_EXPRESSION = new MOgnlElementType("CONDITIONAL_EXPRESSION");
  IElementType EXPRESSION = new MOgnlElementType("EXPRESSION");
  IElementType INDEXED_EXPRESSION = new MOgnlElementType("INDEXED_EXPRESSION");
  IElementType LITERAL_EXPRESSION = new MOgnlElementType("LITERAL_EXPRESSION");
  IElementType METHOD_CALL_EXPRESSION = new MOgnlElementType("METHOD_CALL_EXPRESSION");
  IElementType PARAMETER_LIST = new MOgnlElementType("PARAMETER_LIST");
  IElementType PARENTHESIZED_EXPRESSION = new MOgnlElementType("PARENTHESIZED_EXPRESSION");
  IElementType PROJECTION_EXPRESSION = new MOgnlElementType("PROJECTION_EXPRESSION");
  IElementType REFERENCE_EXPRESSION = new MOgnlElementType("REFERENCE_EXPRESSION");
  IElementType SELECTION_EXPRESSION = new MOgnlElementType("SELECTION_EXPRESSION");
  IElementType UNARY_EXPRESSION = new MOgnlElementType("UNARY_EXPRESSION");
  IElementType VARIABLE_EXPRESSION = new MOgnlElementType("VARIABLE_EXPRESSION");

  IElementType AND = new MOgnlTokenType("&");
  IElementType AND_AND = new MOgnlTokenType("&&");
  IElementType AND_KEYWORD = new MOgnlTokenType("and");
  IElementType AT = new MOgnlTokenType("@");
  IElementType BAND_KEYWORD = new MOgnlTokenType("band");
  IElementType BOR_KEYWORD = new MOgnlTokenType("bor");
  IElementType CHARACTER_LITERAL = new MOgnlTokenType("CHARACTER_LITERAL");
  IElementType COLON = new MOgnlTokenType(":");
  IElementType COMMA = new MOgnlTokenType(",");
  IElementType DIVISION = new MOgnlTokenType("/");
  IElementType DOLLAR = new MOgnlTokenType("$");
  IElementType DOT = new MOgnlTokenType(".");
  IElementType DOUBLE_LITERAL = new MOgnlTokenType("DOUBLE_LITERAL");
  IElementType EQ = new MOgnlTokenType("=");
  IElementType EQUAL = new MOgnlTokenType("==");
  IElementType EQ_KEYWORD = new MOgnlTokenType("eq");
  IElementType EXPRESSION_START = new MOgnlTokenType("${");
  IElementType FALSE_KEYWORD = new MOgnlTokenType("false");
  IElementType GREATER = new MOgnlTokenType(">");
  IElementType GREATER_EQUAL = new MOgnlTokenType(">=");
  IElementType GT_EQ_KEYWORD = new MOgnlTokenType("gte");
  IElementType GT_KEYWORD = new MOgnlTokenType("gt");
  IElementType HASH = new MOgnlTokenType("#");
  IElementType IDENTIFIER = new MOgnlTokenType("IDENTIFIER");
  IElementType INSTANCEOF_KEYWORD = new MOgnlTokenType("instanceof");
  IElementType INTEGER_LITERAL = new MOgnlTokenType("INTEGER_LITERAL");
  IElementType IN_KEYWORD = new MOgnlTokenType("in");
  IElementType LBRACE = new MOgnlTokenType("{");
  IElementType LBRACKET = new MOgnlTokenType("[");
  IElementType LESS = new MOgnlTokenType("<");
  IElementType LESS_EQUAL = new MOgnlTokenType("<=");
  IElementType LPARENTH = new MOgnlTokenType("(");
  IElementType LT_EQ_KEYWORD = new MOgnlTokenType("lte");
  IElementType LT_KEYWORD = new MOgnlTokenType("lt");
  IElementType MINUS = new MOgnlTokenType("-");
  IElementType MODULO = new MOgnlTokenType("%");
  IElementType MULTIPLY = new MOgnlTokenType("*");
  IElementType NEGATE = new MOgnlTokenType("!");
  IElementType NEQ_KEYWORD = new MOgnlTokenType("neq");
  IElementType NEW_KEYWORD = new MOgnlTokenType("new");
  IElementType NOT = new MOgnlTokenType("~");
  IElementType NOT_EQUAL = new MOgnlTokenType("!=");
  IElementType NOT_IN_KEYWORD = new MOgnlTokenType("not in");
  IElementType NOT_KEYWORD = new MOgnlTokenType("not");
  IElementType NULL_KEYWORD = new MOgnlTokenType("null");
  IElementType OR = new MOgnlTokenType("|");
  IElementType OR_KEYWORD = new MOgnlTokenType("or");
  IElementType OR_OR = new MOgnlTokenType("||");
  IElementType PLUS = new MOgnlTokenType("+");
  IElementType QUESTION = new MOgnlTokenType("?");
  IElementType RBRACE = new MOgnlTokenType("}");
  IElementType RBRACKET = new MOgnlTokenType("]");
  IElementType RPARENTH = new MOgnlTokenType(")");
  IElementType SHIFT_LEFT = new MOgnlTokenType("<<");
  IElementType SHIFT_LEFT_KEYWORD = new MOgnlTokenType("shl");
  IElementType SHIFT_RIGHT = new MOgnlTokenType(">>");
  IElementType SHIFT_RIGHT_KEYWORD = new MOgnlTokenType("shr");
  IElementType SHIFT_RIGHT_LOGICAL = new MOgnlTokenType(">>>");
  IElementType SHIFT_RIGHT_LOGICAL_KEYWORD = new MOgnlTokenType("ushr");
  IElementType STRING_LITERAL = new MOgnlTokenType("STRING_LITERAL");
  IElementType TRUE_KEYWORD = new MOgnlTokenType("true");
  IElementType XOR = new MOgnlTokenType("^");
  IElementType XOR_KEYWORD = new MOgnlTokenType("xor");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BINARY_EXPRESSION) {
        return new MOgnlBinaryExpressionImpl(node);
      }
      else if (type == CONDITIONAL_EXPRESSION) {
        return new MOgnlConditionalExpressionImpl(node);
      }
      else if (type == INDEXED_EXPRESSION) {
        return new MOgnlIndexedExpressionImpl(node);
      }
      else if (type == LITERAL_EXPRESSION) {
        return new MOgnlLiteralExpressionImpl(node);
      }
      else if (type == METHOD_CALL_EXPRESSION) {
        return new MOgnlMethodCallExpressionImpl(node);
      }
      else if (type == PARAMETER_LIST) {
        return new MOgnlParameterListImpl(node);
      }
      else if (type == PARENTHESIZED_EXPRESSION) {
        return new MOgnlParenthesizedExpressionImpl(node);
      }
      else if (type == PROJECTION_EXPRESSION) {
        return new MOgnlProjectionExpressionImpl(node);
      }
      else if (type == REFERENCE_EXPRESSION) {
        return new MOgnlReferenceExpressionImpl(node);
      }
      else if (type == SELECTION_EXPRESSION) {
        return new MOgnlSelectionExpressionImpl(node);
      }
      else if (type == UNARY_EXPRESSION) {
        return new MOgnlUnaryExpressionImpl(node);
      }
      else if (type == VARIABLE_EXPRESSION) {
        return new MOgnlVariableExpressionImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
