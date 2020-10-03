package top.totoro.plugin.action;

import com.intellij.ide.impl.NewProjectUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;
import top.totoro.plugin.constant.Constants;
import top.totoro.plugin.util.Log;
import top.totoro.plugin.file.SwingProjectInitializer;
import top.totoro.plugin.ui.NewSwingProjectWrapper;
import top.totoro.plugin.util.ThreadPoolUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class NewSwingProjectAction extends NewSwingModuleAction {

    private static final String TAG = NewSwingProjectAction.class.getSimpleName();

    public NewSwingProjectAction() {
        getTemplatePresentation().setText("新建Swing项目");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        NewSwingProjectWrapper wizard = new NewSwingProjectWrapper(null, ModulesProvider.EMPTY_MODULES_PROVIDER, null);
        NewProjectUtil.createNewProject(wizard);
        Log.d(this, "createNewProject() end");
        String projectPath = wizard.getProjectPath();
        initProjectFiles(e, projectPath);
    }

    private void initProjectFiles(AnActionEvent e, String projectPath) {
        ThreadPoolUtil.execute(() -> {
            File pomFile = new File(projectPath + "/pom.xml");
            StringBuilder content = new StringBuilder();
            FileReader fr = null;
            try {
                fr = new FileReader(pomFile);
                BufferedReader br = new BufferedReader(fr);
                String line = "";
                while ((line = br.readLine()) != null) {
                    content.append(line).append("\n");
                }
            } catch (IOException exception) {
//                exception.printStackTrace();
            } finally {
                // 如果依赖还没有写入的话，需要继续等待写入
                if (!content.toString().contains(Constants.DEPENDENCY)) {
                    // 新项目的文件都是在子线程中创建的
                    // 延迟一段时间后，防止pom.xml文件未创建
                    SwingProjectInitializer.createSwingProjectFiles(projectPath);
                    // 刷新目录
                    if (e.getData(CommonDataKeys.VIRTUAL_FILE) != null) {
                        Objects.requireNonNull(e.getData(CommonDataKeys.VIRTUAL_FILE)).refresh(false, true);
                    }
                    initProjectFiles(e, projectPath);
                }
            }
        }, 1500);
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
