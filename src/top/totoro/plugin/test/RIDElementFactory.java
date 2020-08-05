package top.totoro.plugin.test;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;

public class RIDElementFactory {
    public static RIDProperty createProperty(Project project, String name) {
        final RIDFile file = createFile(project, name);
        return (RIDProperty) file.getFirstChild();
    }

    public static RIDFile createFile(Project project, String text) {
        String name = "dummy.simple";
        return (RIDFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, JavaFileType.INSTANCE, text);
    }
}