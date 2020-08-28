package top.totoro.plugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NewSwingProjectWrapper extends NewSwingModuleWrapper {

    private String projectPath;

    public NewSwingProjectWrapper(@Nullable Project project, @NotNull ModulesProvider modulesProvider, @Nullable String defaultPath) {
        super(project, modulesProvider, defaultPath);
    }

    @Override
    public boolean doFinishAction() {
        boolean superResult = super.doFinishAction();
        projectPath = myWizardContext.getProjectFileDirectory();
        return superResult;
    }

    public String getProjectPath() {
        return projectPath;
    }
}
