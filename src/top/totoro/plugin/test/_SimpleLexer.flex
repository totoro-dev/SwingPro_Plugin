package top.totoro.plugin.test;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static top.totoro.plugin.test.SimpleTypes.*;

%%

%{
  public _SimpleLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _SimpleLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+


%%
<YYINITIAL> {
  {WHITE_SPACE}      { return WHITE_SPACE; }

  "COMMENT"          { return COMMENT; }
  "CRLF"             { return CRLF; }
  "TAG"              { return TAG; }
  "KEY"              { return KEY; }
  "SEPARATOR"        { return SEPARATOR; }
  "VALUE"            { return VALUE; }


}

[^] { return BAD_CHARACTER; }
