package top.totoro.plugin.action;

import com.intellij.ide.impl.NewProjectUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;
import top.totoro.plugin.file.Log;
import top.totoro.plugin.file.SwingProjectInitializer;
import top.totoro.plugin.ui.NewSwingProjectWrapper;

import java.util.Objects;

public class NewSwingProjectAction extends NewSwingModuleAction {

    private static final String TAG = NewSwingProjectAction.class.getSimpleName();

    public NewSwingProjectAction() {
        getTemplatePresentation().setText("新建Swing项目");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        NewSwingProjectWrapper wizard = new NewSwingProjectWrapper((Project) null, ModulesProvider.EMPTY_MODULES_PROVIDER, (String) null);
        NewProjectUtil.createNewProject(wizard);
        Log.d(this, "createNewProject() end");
        String projectPath = wizard.getProjectPath();
        new Thread(() -> {
            try {
                // 新项目的文件都是在子线程中创建的
                // 延迟一段时间后，防止pom.xml文件未创建
                Thread.sleep(1000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            SwingProjectInitializer.createSwingProjectFiles(projectPath);
            // 刷新目录
            if (e.getData(CommonDataKeys.VIRTUAL_FILE) != null) {
                Objects.requireNonNull(e.getData(CommonDataKeys.VIRTUAL_FILE)).refresh(false, true);
            }
        }).start();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }

    @NotNull
    @Override
    public String getActionText(boolean isInNewSubmenu, boolean isInJavaIde) {
        return "创建Swing项目";
    }
}
