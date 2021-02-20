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
package tk.cofe.plugin.mbsp.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static tk.cofe.plugin.mbsp.MbspTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
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
    create_token_set_(BINARY_EXPRESSION, CONDITIONAL_EXPRESSION, EXPRESSION, INDEXED_EXPRESSION,
      LITERAL_EXPRESSION, METHOD_CALL_EXPRESSION, PARENTHESIZED_EXPRESSION, PROJECTION_EXPRESSION,
      REFERENCE_EXPRESSION, SELECTION_EXPRESSION, UNARY_EXPRESSION, VARIABLE_EXPRESSION),
  };

  /* ********************************************************** */
  // plusMinusOperations |
  //                            divideMultiplyOperations |
  //                            bitwiseBooleanOperations |
  //                            instanceOfOperation |
  //                            shiftOperations |
  //                            booleanOperations |
  //                            equalityOperations |
  //                            relationalOperations |
  //                            setOperations
  static boolean binaryOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "binaryOperations")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null, "<operator>");
    r = plusMinusOperations(b, l + 1);
    if (!r) r = divideMultiplyOperations(b, l + 1);
    if (!r) r = bitwiseBooleanOperations(b, l + 1);
    if (!r) r = instanceOfOperation(b, l + 1);
    if (!r) r = shiftOperations(b, l + 1);
    if (!r) r = booleanOperations(b, l + 1);
    if (!r) r = equalityOperations(b, l + 1);
    if (!r) r = relationalOperations(b, l + 1);
    if (!r) r = setOperations(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // "|" | "^" | "&" | "band" | "bor" | "xor"
  static boolean bitwiseBooleanOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitwiseBooleanOperations")) return false;
    boolean r;
    r = consumeToken(b, OR);
    if (!r) r = consumeToken(b, XOR);
    if (!r) r = consumeToken(b, AND);
    if (!r) r = consumeToken(b, BAND_KEYWORD);
    if (!r) r = consumeToken(b, BOR_KEYWORD);
    if (!r) r = consumeToken(b, XOR_KEYWORD);
    return r;
  }

  /* ********************************************************** */
  // '!' | '~'
  static boolean bitwiseOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bitwiseOperations")) return false;
    if (!nextTokenIs(b, "", NEGATE, NOT)) return false;
    boolean r;
    r = consumeToken(b, NEGATE);
    if (!r) r = consumeToken(b, NOT);
    return r;
  }

  /* ********************************************************** */
  // TRUE_KEYWORD | FALSE_KEYWORD
  static boolean booleanLiteralExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "booleanLiteralExpression")) return false;
    if (!nextTokenIs(b, "", FALSE_KEYWORD, TRUE_KEYWORD)) return false;
    boolean r;
    r = consumeToken(b, TRUE_KEYWORD);
    if (!r) r = consumeToken(b, FALSE_KEYWORD);
    return r;
  }

  /* ********************************************************** */
  // "&&" | "||" |
  //                               "and" | "or" |
  //                               "not"
  static boolean booleanOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "booleanOperations")) return false;
    boolean r;
    r = consumeToken(b, AND_AND);
    if (!r) r = consumeToken(b, OR_OR);
    if (!r) r = consumeToken(b, AND_KEYWORD);
    if (!r) r = consumeToken(b, OR_KEYWORD);
    if (!r) r = consumeToken(b, NOT_KEYWORD);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER ('.' IDENTIFIER)*
  static boolean classNameExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classNameExpression")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    r = r && classNameExpression_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('.' IDENTIFIER)*
  private static boolean classNameExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classNameExpression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!classNameExpression_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "classNameExpression_1", c)) break;
    }
    return true;
  }

  // '.' IDENTIFIER
  private static boolean classNameExpression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classNameExpression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOT, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ':' expression
  static boolean conditionalExpressionTail(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "conditionalExpressionTail")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COLON);
    p = r; // pin = 1
    r = r && expression(b, l + 1, -1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // '*' | '/' | '%'
  static boolean divideMultiplyOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "divideMultiplyOperations")) return false;
    boolean r;
    r = consumeToken(b, MULTIPLY);
    if (!r) r = consumeToken(b, DIVISION);
    if (!r) r = consumeToken(b, MODULO);
    return r;
  }

  /* ********************************************************** */
  // "==" | "!=" |
  //                                "eq" | "neq"
  static boolean equalityOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equalityOperations")) return false;
    boolean r;
    r = consumeToken(b, EQUAL);
    if (!r) r = consumeToken(b, NOT_EQUAL);
    if (!r) r = consumeToken(b, EQ_KEYWORD);
    if (!r) r = consumeToken(b, NEQ_KEYWORD);
    return r;
  }

  /* ********************************************************** */
  // "instanceof"
  static boolean instanceOfOperation(PsiBuilder b, int l) {
    return consumeToken(b, INSTANCEOF_KEYWORD);
  }

  /* ********************************************************** */
  // 'javaType' '=' classNameExpression
  public static boolean javaTypeConfig(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "javaTypeConfig")) return false;
    if (!nextTokenIs(b, JAVA_TYPE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, JAVA_TYPE_CONFIG, null);
    r = consumeTokens(b, 1, JAVA_TYPE_KEYWORD, EQ);
    p = r; // pin = 1
    r = r && classNameExpression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'jdbcType' '=' IDENTIFIER
  public static boolean jdbcTypeConfig(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jdbcTypeConfig")) return false;
    if (!nextTokenIs(b, JDBC_TYPE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, JDBC_TYPE_CONFIG, null);
    r = consumeTokens(b, 1, JDBC_TYPE_KEYWORD, EQ, IDENTIFIER);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'jdbcTypeName' '=' IDENTIFIER
  public static boolean jdbcTypeNameConfig(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jdbcTypeNameConfig")) return false;
    if (!nextTokenIs(b, JDBC_TYPE_KEYWORD_NAME)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, JDBC_TYPE_NAME_CONFIG, null);
    r = consumeTokens(b, 1, JDBC_TYPE_KEYWORD_NAME, EQ, IDENTIFIER);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // 'mode' '=' IDENTIFIER
  public static boolean modeConfig(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "modeConfig")) return false;
    if (!nextTokenIs(b, MODE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MODE_CONFIG, null);
    r = consumeTokens(b, 1, MODE_KEYWORD, EQ, IDENTIFIER);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // INTEGER_LITERAL | DOUBLE_LITERAL
  static boolean numberLiteralExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numberLiteralExpression")) return false;
    if (!nextTokenIs(b, "", DOUBLE_LITERAL, INTEGER_LITERAL)) return false;
    boolean r;
    r = consumeToken(b, INTEGER_LITERAL);
    if (!r) r = consumeToken(b, DOUBLE_LITERAL);
    return r;
  }

  /* ********************************************************** */
  // 'numericScale' '=' INTEGER_LITERAL
  public static boolean numericScaleConfig(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "numericScaleConfig")) return false;
    if (!nextTokenIs(b, NUMERIC_SCALE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NUMERIC_SCALE_CONFIG, null);
    r = consumeTokens(b, 1, NUMERIC_SCALE_KEYWORD, EQ, INTEGER_LITERAL);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // (',' (modeConfig | javaTypeConfig | jdbcTypeConfig | jdbcTypeNameConfig | numericScaleConfig | typeHandlerConfig | resultMapConfig))+
  public static boolean paramConfig(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramConfig")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = paramConfig_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!paramConfig_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "paramConfig", c)) break;
    }
    exit_section_(b, m, PARAM_CONFIG, r);
    return r;
  }

  // ',' (modeConfig | javaTypeConfig | jdbcTypeConfig | jdbcTypeNameConfig | numericScaleConfig | typeHandlerConfig | resultMapConfig)
  private static boolean paramConfig_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramConfig_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && paramConfig_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // modeConfig | javaTypeConfig | jdbcTypeConfig | jdbcTypeNameConfig | numericScaleConfig | typeHandlerConfig | resultMapConfig
  private static boolean paramConfig_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramConfig_0_1")) return false;
    boolean r;
    r = modeConfig(b, l + 1);
    if (!r) r = javaTypeConfig(b, l + 1);
    if (!r) r = jdbcTypeConfig(b, l + 1);
    if (!r) r = jdbcTypeNameConfig(b, l + 1);
    if (!r) r = numericScaleConfig(b, l + 1);
    if (!r) r = typeHandlerConfig(b, l + 1);
    if (!r) r = resultMapConfig(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // expression? (',' expression)*
  public static boolean parameterList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterList")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER_LIST, "<parameter list>");
    r = parameterList_0(b, l + 1);
    r = r && parameterList_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expression?
  private static boolean parameterList_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterList_0")) return false;
    expression(b, l + 1, -1);
    return true;
  }

  // (',' expression)*
  private static boolean parameterList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!parameterList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "parameterList_1", c)) break;
    }
    return true;
  }

  // ',' expression
  private static boolean parameterList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameterList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '+' | '-'
  static boolean plusMinusOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "plusMinusOperations")) return false;
    if (!nextTokenIs(b, "", MINUS, PLUS)) return false;
    boolean r;
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    return r;
  }

  /* ********************************************************** */
  // '{' expression '}'
  public static boolean projectionExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projectionExpression")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && expression(b, l + 1, -1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, PROJECTION_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // '<'  | "<="  | '>'  | ">=" |
  //                                  "lt" | "lte" | "gt" | "gte"
  static boolean relationalOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relationalOperations")) return false;
    boolean r;
    r = consumeToken(b, LESS);
    if (!r) r = consumeToken(b, LESS_EQUAL);
    if (!r) r = consumeToken(b, GREATER);
    if (!r) r = consumeToken(b, GREATER_EQUAL);
    if (!r) r = consumeToken(b, LT_KEYWORD);
    if (!r) r = consumeToken(b, LT_EQ_KEYWORD);
    if (!r) r = consumeToken(b, GT_KEYWORD);
    if (!r) r = consumeToken(b, GT_EQ_KEYWORD);
    return r;
  }

  /* ********************************************************** */
  // 'resultMap' '=' IDENTIFIER
  public static boolean resultMapConfig(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resultMapConfig")) return false;
    if (!nextTokenIs(b, RESULT_MAP_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, RESULT_MAP_CONFIG, null);
    r = consumeTokens(b, 1, RESULT_MAP_KEYWORD, EQ, IDENTIFIER);
    p = r; // pin = 1
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // EXPRESSION_START rootElement RBRACE
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    if (!nextTokenIs(b, EXPRESSION_START)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, EXPRESSION_START);
    p = r; // pin = 1
    r = r && report_error_(b, rootElement(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // expression paramConfig?
  static boolean rootElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rootElement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = expression(b, l + 1, -1);
    r = r && rootElement_1(b, l + 1);
    exit_section_(b, l, m, r, false, MbspParser::rootRecover);
    return r;
  }

  // paramConfig?
  private static boolean rootElement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rootElement_1")) return false;
    paramConfig(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // !(RBRACE)
  static boolean rootRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rootRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !consumeToken(b, RBRACE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '{' ('?' | '^' | '$') expression '}'
  public static boolean selectionExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectionExpression")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && selectionExpression_1(b, l + 1);
    r = r && expression(b, l + 1, -1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, SELECTION_EXPRESSION, r);
    return r;
  }

  // '?' | '^' | '$'
  private static boolean selectionExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "selectionExpression_1")) return false;
    boolean r;
    r = consumeToken(b, QUESTION);
    if (!r) r = consumeToken(b, XOR);
    if (!r) r = consumeToken(b, DOLLAR_KEYWORD);
    return r;
  }

  /* ********************************************************** */
  // "not in" | "in"
  static boolean setOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setOperations")) return false;
    if (!nextTokenIs(b, "", IN_KEYWORD, NOT_IN_KEYWORD)) return false;
    boolean r;
    r = consumeToken(b, NOT_IN_KEYWORD);
    if (!r) r = consumeToken(b, IN_KEYWORD);
    return r;
  }

  /* ********************************************************** */
  // "<<" | ">>" | ">>>" |
  //                             "shl" | "shr" | "ushr"
  static boolean shiftOperations(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "shiftOperations")) return false;
    boolean r;
    r = consumeToken(b, SHIFT_LEFT);
    if (!r) r = consumeToken(b, SHIFT_RIGHT);
    if (!r) r = consumeToken(b, SHIFT_RIGHT_LOGICAL);
    if (!r) r = consumeToken(b, SHIFT_LEFT_KEYWORD);
    if (!r) r = consumeToken(b, SHIFT_RIGHT_KEYWORD);
    if (!r) r = consumeToken(b, SHIFT_RIGHT_LOGICAL_KEYWORD);
    return r;
  }

  /* ********************************************************** */
  // STRING_LITERAL | CHARACTER_LITERAL
  static boolean textLiteralExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "textLiteralExpression")) return false;
    if (!nextTokenIs(b, "", CHARACTER_LITERAL, STRING_LITERAL)) return false;
    boolean r;
    r = consumeToken(b, STRING_LITERAL);
    if (!r) r = consumeToken(b, CHARACTER_LITERAL);
    return r;
  }

  /* ********************************************************** */
  // 'typeHandler' '=' classNameExpression
  public static boolean typeHandlerConfig(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeHandlerConfig")) return false;
    if (!nextTokenIs(b, TYPE_HANDLER_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TYPE_HANDLER_CONFIG, null);
    r = consumeTokens(b, 1, TYPE_HANDLER_KEYWORD, EQ);
    p = r; // pin = 1
    r = r && classNameExpression(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // bitwiseOperations |
  //                           '+' | '-' | 'not'
  static boolean unaryOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unaryOperator")) return false;
    boolean r;
    r = bitwiseOperations(b, l + 1);
    if (!r) r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    if (!r) r = consumeToken(b, NOT_KEYWORD);
    return r;
  }

  /* ********************************************************** */
  // Expression root: expression
  // Operator priority table:
  // 0: PREFIX(unaryExpression)
  // 1: ATOM(referenceExpression)
  // 2: ATOM(parenthesizedExpression)
  // 3: BINARY(conditionalExpression)
  // 4: BINARY(binaryExpression)
  // 5: POSTFIX(methodCallExpression)
  // 6: ATOM(indexedExpression)
  // 7: ATOM(variableExpression)
  // 8: ATOM(literalExpression)
  public static boolean expression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expression")) return false;
    addVariant(b, "<expression>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<expression>");
    r = unaryExpression(b, l + 1);
    if (!r) r = referenceExpression(b, l + 1);
    if (!r) r = parenthesizedExpression(b, l + 1);
    if (!r) r = indexedExpression(b, l + 1);
    if (!r) r = variableExpression(b, l + 1);
    if (!r) r = literalExpression(b, l + 1);
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
      if (g < 3 && consumeTokenSmart(b, QUESTION)) {
        r = report_error_(b, expression(b, l, 3));
        r = conditionalExpressionTail(b, l + 1) && r;
        exit_section_(b, l, m, CONDITIONAL_EXPRESSION, r, true, null);
      }
      else if (g < 4 && binaryOperations(b, l + 1)) {
        r = expression(b, l, 4);
        exit_section_(b, l, m, BINARY_EXPRESSION, r, true, null);
      }
      else if (g < 5 && leftMarkerIs(b, REFERENCE_EXPRESSION) && methodCallExpression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, METHOD_CALL_EXPRESSION, r, true, null);
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = unaryOperator(b, l + 1);
    p = r;
    r = p && expression(b, l, 0);
    exit_section_(b, l, m, UNARY_EXPRESSION, r, p, null);
    return r || p;
  }

  // (variableExpression | ('@')? IDENTIFIER)
  //                         ('.' IDENTIFIER)* ('@' IDENTIFIER)?
  //                         ('.'  selectionExpression)?
  //                         ('.'  projectionExpression )?
  public static boolean referenceExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, REFERENCE_EXPRESSION, "<reference expression>");
    r = referenceExpression_0(b, l + 1);
    r = r && referenceExpression_1(b, l + 1);
    r = r && referenceExpression_2(b, l + 1);
    r = r && referenceExpression_3(b, l + 1);
    r = r && referenceExpression_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // variableExpression | ('@')? IDENTIFIER
  private static boolean referenceExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = variableExpression(b, l + 1);
    if (!r) r = referenceExpression_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('@')? IDENTIFIER
  private static boolean referenceExpression_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = referenceExpression_0_1_0(b, l + 1);
    r = r && consumeToken(b, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('@')?
  private static boolean referenceExpression_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_0_1_0")) return false;
    consumeTokenSmart(b, AT_KEYWORD);
    return true;
  }

  // ('.' IDENTIFIER)*
  private static boolean referenceExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!referenceExpression_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "referenceExpression_1", c)) break;
    }
    return true;
  }

  // '.' IDENTIFIER
  private static boolean referenceExpression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokensSmart(b, 0, DOT, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('@' IDENTIFIER)?
  private static boolean referenceExpression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_2")) return false;
    referenceExpression_2_0(b, l + 1);
    return true;
  }

  // '@' IDENTIFIER
  private static boolean referenceExpression_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokensSmart(b, 0, AT_KEYWORD, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('.'  selectionExpression)?
  private static boolean referenceExpression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_3")) return false;
    referenceExpression_3_0(b, l + 1);
    return true;
  }

  // '.'  selectionExpression
  private static boolean referenceExpression_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, DOT);
    r = r && selectionExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('.'  projectionExpression )?
  private static boolean referenceExpression_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_4")) return false;
    referenceExpression_4_0(b, l + 1);
    return true;
  }

  // '.'  projectionExpression
  private static boolean referenceExpression_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, DOT);
    r = r && projectionExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LPARENTH expression RPARENTH
  public static boolean parenthesizedExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parenthesizedExpression")) return false;
    if (!nextTokenIsSmart(b, LPARENTH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PARENTHESIZED_EXPRESSION, null);
    r = consumeTokenSmart(b, LPARENTH);
    p = r; // pin = 1
    r = r && report_error_(b, expression(b, l + 1, -1));
    r = p && consumeToken(b, RPARENTH) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '(' parameterList ')'
  private static boolean methodCallExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodCallExpression_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeTokenSmart(b, LPARENTH);
    p = r; // pin = '\('
    r = r && report_error_(b, parameterList(b, l + 1));
    r = p && consumeToken(b, RPARENTH) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (referenceExpression | variableExpression) '[' expression ']'
  //                       ('.' IDENTIFIER)*
  //                       ('.'  selectionExpression | projectionExpression)?
  public static boolean indexedExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexedExpression")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, INDEXED_EXPRESSION, "<indexed expression>");
    r = indexedExpression_0(b, l + 1);
    r = r && consumeToken(b, LBRACKET);
    p = r; // pin = 2
    r = r && report_error_(b, expression(b, l + 1, -1));
    r = p && report_error_(b, consumeToken(b, RBRACKET)) && r;
    r = p && report_error_(b, indexedExpression_4(b, l + 1)) && r;
    r = p && indexedExpression_5(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // referenceExpression | variableExpression
  private static boolean indexedExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexedExpression_0")) return false;
    boolean r;
    r = referenceExpression(b, l + 1);
    if (!r) r = variableExpression(b, l + 1);
    return r;
  }

  // ('.' IDENTIFIER)*
  private static boolean indexedExpression_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexedExpression_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!indexedExpression_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "indexedExpression_4", c)) break;
    }
    return true;
  }

  // '.' IDENTIFIER
  private static boolean indexedExpression_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexedExpression_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokensSmart(b, 0, DOT, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  // ('.'  selectionExpression | projectionExpression)?
  private static boolean indexedExpression_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexedExpression_5")) return false;
    indexedExpression_5_0(b, l + 1);
    return true;
  }

  // '.'  selectionExpression | projectionExpression
  private static boolean indexedExpression_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexedExpression_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = indexedExpression_5_0_0(b, l + 1);
    if (!r) r = projectionExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '.'  selectionExpression
  private static boolean indexedExpression_5_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "indexedExpression_5_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, DOT);
    r = r && selectionExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '#' IDENTIFIER
  public static boolean variableExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variableExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_EXPRESSION, "<variable expression>");
    r = consumeTokenSmart(b, "#");
    r = r && consumeToken(b, IDENTIFIER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // numberLiteralExpression |
  //                       textLiteralExpression |
  //                       booleanLiteralExpression |
  //                       NULL_KEYWORD
  public static boolean literalExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literalExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_EXPRESSION, "<literal expression>");
    r = numberLiteralExpression(b, l + 1);
    if (!r) r = textLiteralExpression(b, l + 1);
    if (!r) r = booleanLiteralExpression(b, l + 1);
    if (!r) r = consumeTokenSmart(b, NULL_KEYWORD);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
