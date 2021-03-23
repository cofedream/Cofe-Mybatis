package tk.cofe.plugin.mbel.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static tk.cofe.plugin.mbel.MbELTypes.*;

%%

%{
  public _MbELLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _MbELLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

WHITE_SPACE_CHAR=[ \t\n\x0B\f\r]+
INTEGER_LITERAL=[:digit:]*
IDENTIFIER=[:jletter:][:jletterdigit:]*

%%
<YYINITIAL> {
  {WHITE_SPACE}           { return WHITE_SPACE; }

  "#{"                    { return HASH_START; }
  "$"                     { return DOLLAR_KEYWORD; }
  "#"                     { return HASH; }
  "{"                     { return LBRACE; }
  "}"                     { return RBRACE; }
  "."                     { return DOT; }
  ","                     { return COMMA; }
  "="                     { return EQ; }
  "mode"                  { return MODE_KEYWORD; }
  "javaType"              { return JAVA_TYPE_KEYWORD; }
  "jdbcType"              { return JDBC_TYPE_KEYWORD; }
  "jdbcTypeName"          { return JDBC_TYPE_KEYWORD_NAME; }
  "numericScale"          { return NUMERIC_SCALE_KEYWORD; }
  "typeHandler"           { return TYPE_HANDLER_KEYWORD; }
  "resultMap"             { return RESULT_MAP_KEYWORD; }

  {WHITE_SPACE_CHAR}      { return WHITE_SPACE_CHAR; }
  {INTEGER_LITERAL}       { return INTEGER_LITERAL; }
  {IDENTIFIER}            { return IDENTIFIER; }

}

[^] { return BAD_CHARACTER; }
