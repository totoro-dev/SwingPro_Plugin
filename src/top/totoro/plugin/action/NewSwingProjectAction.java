package top.totoro.plugin.action;

import com.intellij.ide.actions.NewProjectAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class NewSwingProjectAction extends NewSwingModuleAction {

    private static final String TAG = NewSwingProjectAction.class.getSimpleName();

    public NewSwingProjectAction() {
//        super("新建Swing项目");
        getTemplatePresentation().setText("新建Swing项目");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        super.actionPerformed(anActionEvent);
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
