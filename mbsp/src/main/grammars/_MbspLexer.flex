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
SPACE=[ \t\n\x0B\f\r]+
VARIABLE=[a-zA-Z0-9']+
NMETHOD=[a-zA-Z0-9]+\([a-zA-Z0-9]*\)

%%
<YYINITIAL> {
  {WHITE_SPACE}           { return WHITE_SPACE; }

  "}"                     { return EXPRESSION_END; }
  "!"                     { return NEGATE; }
  "!="                    { return NOT_EQUAL; }
  "=="                    { return EQUAL; }
  "="                     { return EQ; }
  "<"                     { return LESS; }
  "<="                    { return LESS_EQUAL; }
  ">"                     { return GREATER; }
  ">="                    { return GREATER_EQUAL; }
  "and"                   { return AND_KEYWORD; }
  "or"                    { return OR_KEYWORD; }
  "."                     { return DOT; }
  ","                     { return COMMA; }
  "true"                  { return TRUE_KEYWORD; }
  "false"                 { return FALSE_KEYWORD; }

  {EXPRESSION_START}      { return EXPRESSION_START; }
  {SPACE}                 { return SPACE; }
  {VARIABLE}              { return VARIABLE; }
  {NMETHOD}               { return NMETHOD; }

}

[^] { return BAD_CHARACTER; }
