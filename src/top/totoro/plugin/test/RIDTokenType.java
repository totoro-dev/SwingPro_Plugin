package top.totoro.plugin.test;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class RIDTokenType extends IElementType {
    public RIDTokenType(@NotNull @NonNls String debugName) {
        super(debugName, JavaLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "RIDTokenType." + super.toString();
    }
}
