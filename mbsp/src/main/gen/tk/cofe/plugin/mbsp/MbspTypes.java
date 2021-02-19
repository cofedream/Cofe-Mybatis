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
package tk.cofe.plugin.mbsp;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import tk.cofe.plugin.mbsp.psi.MbspElementType;
import tk.cofe.plugin.mbsp.psi.MbspTokenType;
import tk.cofe.plugin.mbsp.psi.impl.*;

public interface MbspTypes {

  IElementType BINARY_EXPRESSION = new MbspElementType("BINARY_EXPRESSION");
  IElementType CONDITIONAL_EXPRESSION = new MbspElementType("CONDITIONAL_EXPRESSION");
  IElementType EXPRESSION = new MbspElementType("EXPRESSION");
  IElementType INDEXED_EXPRESSION = new MbspElementType("INDEXED_EXPRESSION");
  IElementType LITERAL_EXPRESSION = new MbspElementType("LITERAL_EXPRESSION");
  IElementType METHOD_CALL_EXPRESSION = new MbspElementType("METHOD_CALL_EXPRESSION");
  IElementType PARAMETER_LIST = new MbspElementType("PARAMETER_LIST");
  IElementType PARAM_CONFIG_EXPRESSION = new MbspElementType("PARAM_CONFIG_EXPRESSION");
  IElementType PARENTHESIZED_EXPRESSION = new MbspElementType("PARENTHESIZED_EXPRESSION");
  IElementType PROJECTION_EXPRESSION = new MbspElementType("PROJECTION_EXPRESSION");
  IElementType REFERENCE_EXPRESSION = new MbspElementType("REFERENCE_EXPRESSION");
  IElementType SELECTION_EXPRESSION = new MbspElementType("SELECTION_EXPRESSION");
  IElementType UNARY_EXPRESSION = new MbspElementType("UNARY_EXPRESSION");
  IElementType VARIABLE_EXPRESSION = new MbspElementType("VARIABLE_EXPRESSION");

  IElementType AND = new MbspTokenType("&");
  IElementType AND_AND = new MbspTokenType("&&");
  IElementType AND_KEYWORD = new MbspTokenType("and");
  IElementType AT_KEYWORD = new MbspTokenType("@");
  IElementType BAND_KEYWORD = new MbspTokenType("band");
  IElementType BOR_KEYWORD = new MbspTokenType("bor");
  IElementType CHARACTER_LITERAL = new MbspTokenType("CHARACTER_LITERAL");
  IElementType COLON = new MbspTokenType(":");
  IElementType COMMA = new MbspTokenType(",");
  IElementType DIVISION = new MbspTokenType("/");
  IElementType DOLLAR_KEYWORD = new MbspTokenType("$");
  IElementType DOT = new MbspTokenType(".");
  IElementType DOUBLE_LITERAL = new MbspTokenType("DOUBLE_LITERAL");
  IElementType EQ = new MbspTokenType("=");
  IElementType EQUAL = new MbspTokenType("==");
  IElementType EQ_KEYWORD = new MbspTokenType("eq");
  IElementType EXPRESSION_START = new MbspTokenType("EXPRESSION_START");
  IElementType FALSE_KEYWORD = new MbspTokenType("false");
  IElementType GREATER = new MbspTokenType(">");
  IElementType GREATER_EQUAL = new MbspTokenType(">=");
  IElementType GT_EQ_KEYWORD = new MbspTokenType("gte");
  IElementType GT_KEYWORD = new MbspTokenType("gt");
  IElementType IDENTIFIER = new MbspTokenType("IDENTIFIER");
  IElementType INSTANCEOF_KEYWORD = new MbspTokenType("instanceof");
  IElementType INTEGER_LITERAL = new MbspTokenType("INTEGER_LITERAL");
  IElementType IN_KEYWORD = new MbspTokenType("in");
  IElementType JAVA_TYPE_KEYWORD = new MbspTokenType("javaType");
  IElementType JDBC_TYPE_KEYWORD = new MbspTokenType("jdbcType");
  IElementType JDBC_TYPE_KEYWORD_NAME = new MbspTokenType("jdbcTypeName");
  IElementType LBRACE = new MbspTokenType("{");
  IElementType LBRACKET = new MbspTokenType("[");
  IElementType LESS = new MbspTokenType("<");
  IElementType LESS_EQUAL = new MbspTokenType("<=");
  IElementType LPARENTH = new MbspTokenType("(");
  IElementType LT_EQ_KEYWORD = new MbspTokenType("lte");
  IElementType LT_KEYWORD = new MbspTokenType("lt");
  IElementType MINUS = new MbspTokenType("-");
  IElementType MODE_KEYWORD = new MbspTokenType("mode");
  IElementType MODULO = new MbspTokenType("%");
  IElementType MULTIPLY = new MbspTokenType("*");
  IElementType NEGATE = new MbspTokenType("!");
  IElementType NEQ_KEYWORD = new MbspTokenType("neq");
  IElementType NEW_KEYWORD = new MbspTokenType("new");
  IElementType NOT = new MbspTokenType("~");
  IElementType NOT_EQUAL = new MbspTokenType("!=");
  IElementType NOT_IN_KEYWORD = new MbspTokenType("not in");
  IElementType NOT_KEYWORD = new MbspTokenType("not");
  IElementType NULL_KEYWORD = new MbspTokenType("null");
  IElementType NUMERIC_SCALE_KEYWORD = new MbspTokenType("numericScale");
  IElementType OR = new MbspTokenType("|");
  IElementType OR_KEYWORD = new MbspTokenType("or");
  IElementType OR_OR = new MbspTokenType("||");
  IElementType PLUS = new MbspTokenType("+");
  IElementType QUESTION = new MbspTokenType("?");
  IElementType RBRACE = new MbspTokenType("}");
  IElementType RBRACKET = new MbspTokenType("]");
  IElementType RESULT_MAP_KEYWORD = new MbspTokenType("resultMap");
  IElementType RPARENTH = new MbspTokenType(")");
  IElementType SHIFT_LEFT = new MbspTokenType("<<");
  IElementType SHIFT_LEFT_KEYWORD = new MbspTokenType("shl");
  IElementType SHIFT_RIGHT = new MbspTokenType(">>");
  IElementType SHIFT_RIGHT_KEYWORD = new MbspTokenType("shr");
  IElementType SHIFT_RIGHT_LOGICAL = new MbspTokenType(">>>");
  IElementType SHIFT_RIGHT_LOGICAL_KEYWORD = new MbspTokenType("ushr");
  IElementType STRING_LITERAL = new MbspTokenType("STRING_LITERAL");
  IElementType TRUE_KEYWORD = new MbspTokenType("true");
  IElementType TYPE_HANDLER_KEYWORD = new MbspTokenType("typeHandler");
  IElementType XOR = new MbspTokenType("^");
  IElementType XOR_KEYWORD = new MbspTokenType("xor");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BINARY_EXPRESSION) {
        return new MbspBinaryExpressionImpl(node);
      }
      else if (type == CONDITIONAL_EXPRESSION) {
        return new MbspConditionalExpressionImpl(node);
      }
      else if (type == INDEXED_EXPRESSION) {
        return new MbspIndexedExpressionImpl(node);
      }
      else if (type == LITERAL_EXPRESSION) {
        return new MbspLiteralExpressionImpl(node);
      }
      else if (type == METHOD_CALL_EXPRESSION) {
        return new MbspMethodCallExpressionImpl(node);
      }
      else if (type == PARAMETER_LIST) {
        return new MbspParameterListImpl(node);
      }
      else if (type == PARAM_CONFIG_EXPRESSION) {
        return new MbspParamConfigExpressionImpl(node);
      }
      else if (type == PARENTHESIZED_EXPRESSION) {
        return new MbspParenthesizedExpressionImpl(node);
      }
      else if (type == PROJECTION_EXPRESSION) {
        return new MbspProjectionExpressionImpl(node);
      }
      else if (type == REFERENCE_EXPRESSION) {
        return new MbspReferenceExpressionImpl(node);
      }
      else if (type == SELECTION_EXPRESSION) {
        return new MbspSelectionExpressionImpl(node);
      }
      else if (type == UNARY_EXPRESSION) {
        return new MbspUnaryExpressionImpl(node);
      }
      else if (type == VARIABLE_EXPRESSION) {
        return new MbspVariableExpressionImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
