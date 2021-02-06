package tk.cofe.plugin.mbsp.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static tk.cofe.plugin.mbsp.MbspTypes.*;

%%

%{
  public _MbspLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _MbspLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

EXPRESSION_START=[$#]\{
WHITE_SPACE_CHAR=[ \t\n\x0B\f\r]+
//IDENTIFIER=\p{javaJavaIdentifierStart}\p{javaJavaIdentifierPart}*
IDENTIFIER=[:jletter:][:jletterdigit:]
INTEGER_LITERAL=[:digit:]*
DOUBLE_LITERAL=[:digit:]+\.[:digit:]+
CHARACTER_LITERAL='[^\r\n]*'
STRING_LITERAL=\"[^\r\n]*\"

%%
<YYINITIAL> {
  {WHITE_SPACE}            { return WHITE_SPACE; }

  "."                      { return DOT; }
  ","                      { return COMMA; }
  "("                      { return LPARENTH; }
  ")"                      { return RPARENTH; }
  "["                      { return LBRACKET; }
  "]"                      { return RBRACKET; }
  "{"                      { return LBRACE; }
  "}"                      { return RBRACE; }
  "+"                      { return PLUS; }
  "-"                      { return MINUS; }
  "*"                      { return MULTIPLY; }
  "/"                      { return DIVISION; }
  "%"                      { return MODULO; }
  "!"                      { return NEGATE; }
  "!="                     { return NOT_EQUAL; }
  "=="                     { return EQUAL; }
  "="                      { return EQ; }
  "<"                      { return LESS; }
  "<="                     { return LESS_EQUAL; }
  ">"                      { return GREATER; }
  ">="                     { return GREATER_EQUAL; }
  "eq"                     { return EQ_KEYWORD; }
  "neq"                    { return NEQ_KEYWORD; }
  "lt"                     { return LT_KEYWORD; }
  "lte"                    { return LT_EQ_KEYWORD; }
  "gt"                     { return GT_KEYWORD; }
  "gte"                    { return GT_EQ_KEYWORD; }
  "&&"                     { return AND_AND; }
  "||"                     { return OR_OR; }
  "and"                    { return AND_KEYWORD; }
  "or"                     { return OR_KEYWORD; }
  "not"                    { return NOT_KEYWORD; }
  "|"                      { return OR; }
  "^"                      { return XOR; }
  "&"                      { return AND; }
  "~"                      { return NOT; }
  "band"                   { return BAND_KEYWORD; }
  "bor"                    { return BOR_KEYWORD; }
  "xor"                    { return XOR_KEYWORD; }
  "new"                    { return NEW_KEYWORD; }
  "true"                   { return TRUE_KEYWORD; }
  "false"                  { return FALSE_KEYWORD; }
  "null"                   { return NULL_KEYWORD; }
  "instanceof"             { return INSTANCEOF_KEYWORD; }
  "not in"                 { return NOT_IN_KEYWORD; }
  "in"                     { return IN_KEYWORD; }
  "<<"                     { return SHIFT_LEFT; }
  ">>"                     { return SHIFT_RIGHT; }
  ">>>"                    { return SHIFT_RIGHT_LOGICAL; }
  "shl"                    { return SHIFT_LEFT_KEYWORD; }
  "shr"                    { return SHIFT_RIGHT_KEYWORD; }
  "ushr"                   { return SHIFT_RIGHT_LOGICAL_KEYWORD; }
  "@"                      { return AT_KEYWORD; }
  "$"                      { return DOLLAR_KEYWORD; }
  "?"                      { return QUESTION; }
  ":"                      { return COLON; }

  {EXPRESSION_START}       { return EXPRESSION_START; }
  {WHITE_SPACE_CHAR}       { return WHITE_SPACE_CHAR; }
  {IDENTIFIER}             { return IDENTIFIER; }
  {INTEGER_LITERAL}        { return INTEGER_LITERAL; }
  {DOUBLE_LITERAL}         { return DOUBLE_LITERAL; }
  {CHARACTER_LITERAL}      { return CHARACTER_LITERAL; }
  {STRING_LITERAL}         { return STRING_LITERAL; }

}

[^] { return BAD_CHARACTER; }
