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
package tk.cofe.plugin.mbel.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static tk.cofe.plugin.mbel.MbELTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class MbELParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
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
  // !('}' | ',')
  static boolean configRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "configRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !configRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}' | ','
  private static boolean configRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "configRecover_0")) return false;
    boolean r;
    r = consumeToken(b, RBRACE);
    if (!r) r = consumeToken(b, COMMA);
    return r;
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
  // ',' (modeConfig | javaTypeConfig | jdbcTypeConfig | jdbcTypeNameConfig | numericScaleConfig | typeHandlerConfig | resultMapConfig)
  static boolean paramConfig(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramConfig")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, COMMA);
    p = r; // pin = 1
    r = r && paramConfig_1(b, l + 1);
    exit_section_(b, l, m, r, p, MbELParser::configRecover);
    return r || p;
  }

  // modeConfig | javaTypeConfig | jdbcTypeConfig | jdbcTypeNameConfig | numericScaleConfig | typeHandlerConfig | resultMapConfig
  private static boolean paramConfig_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramConfig_1")) return false;
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
  // paramConfig+
  public static boolean paramConfigList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paramConfigList")) return false;
    if (!nextTokenIs(b, COMMA)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = paramConfig(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!paramConfig(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "paramConfigList", c)) break;
    }
    exit_section_(b, m, PARAM_CONFIG_LIST, r);
    return r;
  }

  /* ********************************************************** */
  // classNameExpression
  public static boolean referenceExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, REFERENCE_EXPRESSION, "<reference expression>");
    r = classNameExpression(b, l + 1);
    exit_section_(b, l, m, r, false, MbELParser::referenceRecover);
    return r;
  }

  /* ********************************************************** */
  // !('}' | ',')
  static boolean referenceRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !referenceRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '}' | ','
  private static boolean referenceRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "referenceRecover_0")) return false;
    boolean r;
    r = consumeToken(b, RBRACE);
    if (!r) r = consumeToken(b, COMMA);
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
  // (HASH_START | DOLLAR_START) rootElement RBRACE
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    if (!nextTokenIs(b, "", DOLLAR_START, HASH_START)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = root_0(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, rootElement(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // HASH_START | DOLLAR_START
  private static boolean root_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_0")) return false;
    boolean r;
    r = consumeToken(b, HASH_START);
    if (!r) r = consumeToken(b, DOLLAR_START);
    return r;
  }

  /* ********************************************************** */
  // referenceExpression paramConfigList?
  static boolean rootElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rootElement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = referenceExpression(b, l + 1);
    r = r && rootElement_1(b, l + 1);
    exit_section_(b, l, m, r, false, MbELParser::rootRecover);
    return r;
  }

  // paramConfigList?
  private static boolean rootElement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rootElement_1")) return false;
    paramConfigList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // !(RBRACE | ',')
  static boolean rootRecover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rootRecover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !rootRecover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // RBRACE | ','
  private static boolean rootRecover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rootRecover_0")) return false;
    boolean r;
    r = consumeToken(b, RBRACE);
    if (!r) r = consumeToken(b, COMMA);
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

}
