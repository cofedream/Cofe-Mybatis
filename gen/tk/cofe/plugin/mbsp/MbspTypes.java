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
package tk.cofe.plugin.mbsp;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import tk.cofe.plugin.mbsp.psi.MbspElementType;
import tk.cofe.plugin.mbsp.psi.MbspTokenType;
import tk.cofe.plugin.mbsp.psi.impl.*;

public interface MbspTypes {

  IElementType BINARY_EXPRESSION = new MbspElementType("BINARY_EXPRESSION");
  IElementType EXPRESSION = new MbspElementType("EXPRESSION");
  IElementType REFERENCE_EXPRESSION = new MbspElementType("REFERENCE_EXPRESSION");

  IElementType AND_KEYWORD = new MbspTokenType("and");
  IElementType DOT = new MbspTokenType(".");
  IElementType EQUAL = new MbspTokenType("==");
  IElementType EXPRESSION_END = new MbspTokenType("}");
  IElementType EXPRESSION_START = new MbspTokenType("EXPRESSION_START");
  IElementType NMETHOD = new MbspTokenType("NMETHOD");
  IElementType NOT_EQUAL = new MbspTokenType("!=");
  IElementType OR_KEYWORD = new MbspTokenType("or");
  IElementType VARIABLE = new MbspTokenType("VARIABLE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BINARY_EXPRESSION) {
        return new MbspBinaryExpressionImpl(node);
      }
      else if (type == REFERENCE_EXPRESSION) {
        return new MbspReferenceExpressionImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
