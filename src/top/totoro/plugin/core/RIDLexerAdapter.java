package top.totoro.plugin.core;

import com.intellij.lexer.FlexAdapter;

public class RIDLexerAdapter  extends FlexAdapter {
    public RIDLexerAdapter() {
        super(new RIDLexer(null));
    }
}
