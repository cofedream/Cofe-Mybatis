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
package tk.cofe.plugin.mbl;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import tk.cofe.plugin.mbl.psi.MblElementType;
import tk.cofe.plugin.mbl.psi.MblTokenType;
import tk.cofe.plugin.mbl.psi.impl.*;

public interface MblTypes {

  IElementType BINARY_EXPRESSION = new MblElementType("BINARY_EXPRESSION");
  IElementType CONDITIONAL_EXPRESSION = new MblElementType("CONDITIONAL_EXPRESSION");
  IElementType EXPRESSION = new MblElementType("EXPRESSION");
  IElementType INDEXED_EXPRESSION = new MblElementType("INDEXED_EXPRESSION");
  IElementType JAVA_TYPE_CONFIG = new MblElementType("JAVA_TYPE_CONFIG");
  IElementType JDBC_TYPE_CONFIG = new MblElementType("JDBC_TYPE_CONFIG");
  IElementType JDBC_TYPE_NAME_CONFIG = new MblElementType("JDBC_TYPE_NAME_CONFIG");
  IElementType LITERAL_EXPRESSION = new MblElementType("LITERAL_EXPRESSION");
  IElementType METHOD_CALL_EXPRESSION = new MblElementType("METHOD_CALL_EXPRESSION");
  IElementType MODE_CONFIG = new MblElementType("MODE_CONFIG");
  IElementType NUMERIC_SCALE_CONFIG = new MblElementType("NUMERIC_SCALE_CONFIG");
  IElementType PARAMETER_LIST = new MblElementType("PARAMETER_LIST");
  IElementType PARAM_CONFIG_LIST = new MblElementType("PARAM_CONFIG_LIST");
  IElementType PARENTHESIZED_EXPRESSION = new MblElementType("PARENTHESIZED_EXPRESSION");
  IElementType PROJECTION_EXPRESSION = new MblElementType("PROJECTION_EXPRESSION");
  IElementType REFERENCE_EXPRESSION = new MblElementType("REFERENCE_EXPRESSION");
  IElementType RESULT_MAP_CONFIG = new MblElementType("RESULT_MAP_CONFIG");
  IElementType SELECTION_EXPRESSION = new MblElementType("SELECTION_EXPRESSION");
  IElementType TYPE_HANDLER_CONFIG = new MblElementType("TYPE_HANDLER_CONFIG");
  IElementType UNARY_EXPRESSION = new MblElementType("UNARY_EXPRESSION");
  IElementType VARIABLE_EXPRESSION = new MblElementType("VARIABLE_EXPRESSION");

  IElementType AND = new MblTokenType("&");
  IElementType AND_AND = new MblTokenType("&&");
  IElementType AND_KEYWORD = new MblTokenType("and");
  IElementType AT_KEYWORD = new MblTokenType("@");
  IElementType BAND_KEYWORD = new MblTokenType("band");
  IElementType BOR_KEYWORD = new MblTokenType("bor");
  IElementType CHARACTER_LITERAL = new MblTokenType("CHARACTER_LITERAL");
  IElementType COLON = new MblTokenType(":");
  IElementType COMMA = new MblTokenType(",");
  IElementType DIVISION = new MblTokenType("/");
  IElementType DOLLAR_KEYWORD = new MblTokenType("$");
  IElementType DOT = new MblTokenType(".");
  IElementType DOUBLE_LITERAL = new MblTokenType("DOUBLE_LITERAL");
  IElementType EQ = new MblTokenType("=");
  IElementType EQUAL = new MblTokenType("==");
  IElementType EQ_KEYWORD = new MblTokenType("eq");
  IElementType EXPRESSION_START = new MblTokenType("EXPRESSION_START");
  IElementType FALSE_KEYWORD = new MblTokenType("false");
  IElementType GREATER = new MblTokenType(">");
  IElementType GREATER_EQUAL = new MblTokenType(">=");
  IElementType GT_EQ_KEYWORD = new MblTokenType("gte");
  IElementType GT_KEYWORD = new MblTokenType("gt");
  IElementType IDENTIFIER = new MblTokenType("IDENTIFIER");
  IElementType INSTANCEOF_KEYWORD = new MblTokenType("instanceof");
  IElementType INTEGER_LITERAL = new MblTokenType("INTEGER_LITERAL");
  IElementType IN_KEYWORD = new MblTokenType("in");
  IElementType JAVA_TYPE_KEYWORD = new MblTokenType("javaType");
  IElementType JDBC_TYPE_KEYWORD = new MblTokenType("jdbcType");
  IElementType JDBC_TYPE_KEYWORD_NAME = new MblTokenType("jdbcTypeName");
  IElementType LBRACE = new MblTokenType("{");
  IElementType LBRACKET = new MblTokenType("[");
  IElementType LESS = new MblTokenType("<");
  IElementType LESS_EQUAL = new MblTokenType("<=");
  IElementType LPARENTH = new MblTokenType("(");
  IElementType LT_EQ_KEYWORD = new MblTokenType("lte");
  IElementType LT_KEYWORD = new MblTokenType("lt");
  IElementType MINUS = new MblTokenType("-");
  IElementType MODE_KEYWORD = new MblTokenType("mode");
  IElementType MODULO = new MblTokenType("%");
  IElementType MULTIPLY = new MblTokenType("*");
  IElementType NEGATE = new MblTokenType("!");
  IElementType NEQ_KEYWORD = new MblTokenType("neq");
  IElementType NEW_KEYWORD = new MblTokenType("new");
  IElementType NOT = new MblTokenType("~");
  IElementType NOT_EQUAL = new MblTokenType("!=");
  IElementType NOT_IN_KEYWORD = new MblTokenType("not in");
  IElementType NOT_KEYWORD = new MblTokenType("not");
  IElementType NULL_KEYWORD = new MblTokenType("null");
  IElementType NUMERIC_SCALE_KEYWORD = new MblTokenType("numericScale");
  IElementType OR = new MblTokenType("|");
  IElementType OR_KEYWORD = new MblTokenType("or");
  IElementType OR_OR = new MblTokenType("||");
  IElementType PLUS = new MblTokenType("+");
  IElementType QUESTION = new MblTokenType("?");
  IElementType RBRACE = new MblTokenType("}");
  IElementType RBRACKET = new MblTokenType("]");
  IElementType RESULT_MAP_KEYWORD = new MblTokenType("resultMap");
  IElementType RPARENTH = new MblTokenType(")");
  IElementType SHIFT_LEFT = new MblTokenType("<<");
  IElementType SHIFT_LEFT_KEYWORD = new MblTokenType("shl");
  IElementType SHIFT_RIGHT = new MblTokenType(">>");
  IElementType SHIFT_RIGHT_KEYWORD = new MblTokenType("shr");
  IElementType SHIFT_RIGHT_LOGICAL = new MblTokenType(">>>");
  IElementType SHIFT_RIGHT_LOGICAL_KEYWORD = new MblTokenType("ushr");
  IElementType STRING_LITERAL = new MblTokenType("STRING_LITERAL");
  IElementType TRUE_KEYWORD = new MblTokenType("true");
  IElementType TYPE_HANDLER_KEYWORD = new MblTokenType("typeHandler");
  IElementType XOR = new MblTokenType("^");
  IElementType XOR_KEYWORD = new MblTokenType("xor");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BINARY_EXPRESSION) {
        return new MblBinaryExpressionImpl(node);
      }
      else if (type == CONDITIONAL_EXPRESSION) {
        return new MblConditionalExpressionImpl(node);
      }
      else if (type == INDEXED_EXPRESSION) {
        return new MblIndexedExpressionImpl(node);
      }
      else if (type == JAVA_TYPE_CONFIG) {
        return new MblJavaTypeConfigImpl(node);
      }
      else if (type == JDBC_TYPE_CONFIG) {
        return new MblJdbcTypeConfigImpl(node);
      }
      else if (type == JDBC_TYPE_NAME_CONFIG) {
        return new MblJdbcTypeNameConfigImpl(node);
      }
      else if (type == LITERAL_EXPRESSION) {
        return new MblLiteralExpressionImpl(node);
      }
      else if (type == METHOD_CALL_EXPRESSION) {
        return new MblMethodCallExpressionImpl(node);
      }
      else if (type == MODE_CONFIG) {
        return new MblModeConfigImpl(node);
      }
      else if (type == NUMERIC_SCALE_CONFIG) {
        return new MblNumericScaleConfigImpl(node);
      }
      else if (type == PARAMETER_LIST) {
        return new MblParameterListImpl(node);
      }
      else if (type == PARAM_CONFIG_LIST) {
        return new MblParamConfigListImpl(node);
      }
      else if (type == PARENTHESIZED_EXPRESSION) {
        return new MblParenthesizedExpressionImpl(node);
      }
      else if (type == PROJECTION_EXPRESSION) {
        return new MblProjectionExpressionImpl(node);
      }
      else if (type == REFERENCE_EXPRESSION) {
        return new MblReferenceExpressionImpl(node);
      }
      else if (type == RESULT_MAP_CONFIG) {
        return new MblResultMapConfigImpl(node);
      }
      else if (type == SELECTION_EXPRESSION) {
        return new MblSelectionExpressionImpl(node);
      }
      else if (type == TYPE_HANDLER_CONFIG) {
        return new MblTypeHandlerConfigImpl(node);
      }
      else if (type == UNARY_EXPRESSION) {
        return new MblUnaryExpressionImpl(node);
      }
      else if (type == VARIABLE_EXPRESSION) {
        return new MblVariableExpressionImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
