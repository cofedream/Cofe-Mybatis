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
package tk.cofe.plugin.mbel;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import tk.cofe.plugin.mbel.psi.MbELElementType;
import tk.cofe.plugin.mbel.psi.MbELTokenType;
import tk.cofe.plugin.mbel.psi.impl.*;

public interface MbELTypes {

  IElementType JAVA_TYPE_CONFIG = new MbELElementType("JAVA_TYPE_CONFIG");
  IElementType JDBC_TYPE_CONFIG = new MbELElementType("JDBC_TYPE_CONFIG");
  IElementType JDBC_TYPE_NAME_CONFIG = new MbELElementType("JDBC_TYPE_NAME_CONFIG");
  IElementType MODE_CONFIG = new MbELElementType("MODE_CONFIG");
  IElementType NUMERIC_SCALE_CONFIG = new MbELElementType("NUMERIC_SCALE_CONFIG");
  IElementType PARAM_CONFIG_LIST = new MbELElementType("PARAM_CONFIG_LIST");
  IElementType REFERENCE_EXPRESSION = new MbELElementType("REFERENCE_EXPRESSION");
  IElementType RESULT_MAP_CONFIG = new MbELElementType("RESULT_MAP_CONFIG");
  IElementType TYPE_HANDLER_CONFIG = new MbELElementType("TYPE_HANDLER_CONFIG");

  IElementType COMMA = new MbELTokenType(",");
  IElementType DOLLAR_KEYWORD = new MbELTokenType("$");
  IElementType DOLLAR_START = new MbELTokenType("#{");
  IElementType DOT = new MbELTokenType(".");
  IElementType EQ = new MbELTokenType("=");
  IElementType HASH = new MbELTokenType("#");
  IElementType HASH_START = new MbELTokenType("HASH_START");
  IElementType IDENTIFIER = new MbELTokenType("IDENTIFIER");
  IElementType INTEGER_LITERAL = new MbELTokenType("INTEGER_LITERAL");
  IElementType JAVA_TYPE_KEYWORD = new MbELTokenType("javaType");
  IElementType JDBC_TYPE_KEYWORD = new MbELTokenType("jdbcType");
  IElementType JDBC_TYPE_KEYWORD_NAME = new MbELTokenType("jdbcTypeName");
  IElementType LBRACE = new MbELTokenType("{");
  IElementType MODE_KEYWORD = new MbELTokenType("mode");
  IElementType NUMERIC_SCALE_KEYWORD = new MbELTokenType("numericScale");
  IElementType RBRACE = new MbELTokenType("}");
  IElementType RESULT_MAP_KEYWORD = new MbELTokenType("resultMap");
  IElementType TYPE_HANDLER_KEYWORD = new MbELTokenType("typeHandler");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == JAVA_TYPE_CONFIG) {
        return new MbELJavaTypeConfigImpl(node);
      }
      else if (type == JDBC_TYPE_CONFIG) {
        return new MbELJdbcTypeConfigImpl(node);
      }
      else if (type == JDBC_TYPE_NAME_CONFIG) {
        return new MbELJdbcTypeNameConfigImpl(node);
      }
      else if (type == MODE_CONFIG) {
        return new MbELModeConfigImpl(node);
      }
      else if (type == NUMERIC_SCALE_CONFIG) {
        return new MbELNumericScaleConfigImpl(node);
      }
      else if (type == PARAM_CONFIG_LIST) {
        return new MbELParamConfigListImpl(node);
      }
      else if (type == REFERENCE_EXPRESSION) {
        return new MbELReferenceExpressionImpl(node);
      }
      else if (type == RESULT_MAP_CONFIG) {
        return new MbELResultMapConfigImpl(node);
      }
      else if (type == TYPE_HANDLER_CONFIG) {
        return new MbELTypeHandlerConfigImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
