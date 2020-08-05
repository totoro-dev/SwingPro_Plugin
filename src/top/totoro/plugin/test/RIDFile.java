package top.totoro.plugin.test;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class RIDFile  extends PsiFileBase {
    public RIDFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, JavaLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return JavaFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "JAVA";
    }
}