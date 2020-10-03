package top.totoro.plugin.file;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public class SwingProjectOpenListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        SwingProjectInfo.setProject(project);
    }

    @Override
    public void projectClosing(@NotNull Project project) {
        SwingProjectInfo.closedProject(project);
    }
}
