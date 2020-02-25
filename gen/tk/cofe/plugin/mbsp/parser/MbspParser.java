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
package tk.cofe.plugin.mbsp.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static tk.cofe.plugin.mbsp.MbspTypes.*;
import static tk.cofe.plugin.mbsp.parser.MbspParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class MbspParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(BINARY_EXPRESSION, EXPRESSION, REFERENCE_EXPRESSION, UNARY_EXPRESSION),
  };

  /* ********************************************************** */
  // booleanOperations |
  //                            equalityOperations |
  //                            relationalOperations
  static boolean binaryOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryOperations")) return false;
    boolean r;
    r = booleanOperations(b, l + 1);
    if (!r) r = equalityOperations(b, l + 1);
    if (!r) r = relationalOperations(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // '!'
  static boolean bitwiseOperations(PsiBuilder b, int l) {
    return consumeToken(b, NEGATE);
  }

  /* ********************************************************** */
  // "and" | "or"
  static boolean booleanOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "booleanOperations")) return false;
    if (!nextTokenIs(b, "", AND_KEYWORD, OR_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AND_KEYWORD);
    if (!r) r = consumeToken(b, OR_KEYWORD);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // "==" | "!="
  static boolean equalityOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equalityOperations")) return false;
    if (!nextTokenIs(b, "", EQUAL, NOT_EQUAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQUAL);
    if (!r) r = consumeToken(b, NOT_EQUAL);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '<'  | "<="  | '>'  | ">="
  static boolean relationalOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalOperations")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LESS);
    if (!r) r = consumeToken(b, LESS_EQUAL);
    if (!r) r = consumeToken(b, GREATER);
    if (!r) r = consumeToken(b, GREATER_EQUAL);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // EXPRESSION_START rootElement EXPRESSION_END
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    if (!nextTokenIs(b, EXPRESSION_START)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, EXPRESSION_START);
    p = r; // pin = 1
    r = r && report_error_(b, rootElement(b, l + 1));
    r = p && consumeToken(b, EXPRESSION_END) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // expression
  static boolean rootElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rootElement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, rootRecover_parser_);
    return r;
  }

  /* ********************************************************** */
  // !(EXPRESSION_END)
  static boolean rootRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rootRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeToken(b, EXPRESSION_END);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Expression root: expression
  // Operator priority table:
  // 0: PREFIX(unaryExpression)
  // 1: ATOM(referenceExpression)
  // 2: BINARY(binaryExpression)
  public static boolean expression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expression")) return false;
    addVariant(b, "<expression>");
    if (!nextTokenIsSmart(b, VARIABLE) &&
        !nextTokenIs(b, "<expression>", NEGATE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<expression>");
    r = unaryExpression(b, l + 1);
    if (!r) r = referenceExpression(b, l + 1);
    p = r;
    r = r && expression_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean expression_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expression_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 2 && binaryOperations(b, l + 1)) {
        r = expression(b, l, 2);
        exit_section_(b, l, m, BINARY_EXPRESSION, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  public static boolean unaryExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryExpression")) return false;
    if (!nextTokenIsSmart(b, NEGATE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = bitwiseOperations(b, l + 1);
    p = r;
    r = p && expression(b, l, 0);
    exit_section_(b, l, m, UNARY_EXPRESSION, r, p, null);
    return r || p;
  }

  // VARIABLE
  //                         ('.' VARIABLE | '.' NMETHOD)*
  public static boolean referenceExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression")) return false;
    if (!nextTokenIsSmart(b, VARIABLE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, VARIABLE);
    r = r && referenceExpression_1(b, l + 1);
    exit_section_(b, m, REFERENCE_EXPRESSION, r);
    return r;
  }

  // ('.' VARIABLE | '.' NMETHOD)*
  private static boolean referenceExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!referenceExpression_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "referenceExpression_1", c)) break;
    }
    return true;
  }

  // '.' VARIABLE | '.' NMETHOD
  private static boolean referenceExpression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokensSmart(b, 0, DOT, VARIABLE);
    if (!r) r = parseTokensSmart(b, 0, DOT, NMETHOD);
    exit_section_(b, m, null, r);
    return r;
  }

  static final Parser rootRecover_parser_ = new Parser() {
    public boolean parse(PsiBuilder b, int l) {
      return rootRecover(b, l + 1);
    }
  };
}
