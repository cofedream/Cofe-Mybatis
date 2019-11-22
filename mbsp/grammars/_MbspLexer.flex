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

VARIABLE=[a-zA-Z0-9]+

%%
<YYINITIAL> {
  {WHITE_SPACE}      { return WHITE_SPACE; }

  "${"               { return EXPRESSION_START; }
  "#{"               { return EXPRESSION_PREPARED_START; }
  "}"                { return EXPRESSION_END; }
  "."                { return DOT; }

  {VARIABLE}         { return VARIABLE; }

}

[^] { return BAD_CHARACTER; }