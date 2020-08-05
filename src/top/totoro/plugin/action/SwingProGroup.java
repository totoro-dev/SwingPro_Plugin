package top.totoro.plugin.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import top.totoro.plugin.file.Log;

public class SwingProGroup extends DefaultActionGroup {

    private static final String TAG = SwingProGroup.class.getSimpleName();
    private static final NewSwingProjectAction NEW_SWING_PROJECT_ACTION = new NewSwingProjectAction();
    private static final NewSwingModuleAction NEW_SWING_MODULE_ACTION = new NewSwingModuleAction();
    private static final NewActivityFileAction NEW_ACTIVITY_FILE_ACTION = new NewActivityFileAction();
    private static final NewSwingFileAction NEW_SWING_FILE_ACTION = new NewSwingFileAction();

    public SwingProGroup() {
        add(NEW_SWING_PROJECT_ACTION);
//        add(NEW_SWING_MODULE_ACTION);
        add(NEW_ACTIVITY_FILE_ACTION);
        add(NEW_SWING_FILE_ACTION);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Log.d(TAG, "actionPerformed : " + e.getPlace());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        Log.d(TAG, "update : " + e.getPlace() + ", " + e.getInputEvent());
        DataContext dataContext = e.getDataContext();
        VirtualFile root = DataKeys.VIRTUAL_FILE.getData(dataContext);
        if (root == null) return;
        String chooseDirPath = root.getPath();
        NEW_SWING_FILE_ACTION.setPath(chooseDirPath);
        NEW_SWING_FILE_ACTION.setChooseFile(root);
        NEW_ACTIVITY_FILE_ACTION.setPath(chooseDirPath);
        NEW_ACTIVITY_FILE_ACTION.setChooseFile(root);
    }

}
