package top.totoro.plugin.test;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class RIDLexerAdapter  extends FlexAdapter {
    public RIDLexerAdapter() {
        super(new RIDLexer(null));
    }
}
