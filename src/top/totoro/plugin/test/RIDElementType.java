package top.totoro.plugin.test;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class RIDElementType  extends IElementType {
    public RIDElementType( @NotNull @NonNls String debugName) {
        super(debugName, JavaLanguage.INSTANCE);
    }
}