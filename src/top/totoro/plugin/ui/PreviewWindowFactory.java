package top.totoro.plugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import org.jetbrains.annotations.NotNull;
import top.totoro.plugin.core.SimpleFileType;
import top.totoro.plugin.util.Log;
import top.totoro.plugin.util.ThreadPoolUtil;

/**
 * 创建时间 2020/10/3 15:41
 *
 * @author dragon
 * @version 1.0
 */
public class PreviewWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        refresh(project, toolWindow);
    }

    private void refresh(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        if (project.isDisposed()) return;
        try {
            if (!SimpleFileType.updated) return;
            SimpleFileType.updated = false;
            ApplicationManager.getApplication().invokeLater(() -> {
                // 将虚拟文件保存到物理文件中，避免做出的修改无法被加载到布局中
                FileDocumentManager documentManager = FileDocumentManager.getInstance();
                documentManager.saveAllDocuments();
                Log.d(this, "SimpleFileType.currentFile : " + SimpleFileType.currentFile);
                PreviewWindow previewWindow = new PreviewWindow();
                previewWindow.setResPath(SimpleFileType.currentFile);
                toolWindow.getContentManager().removeAllContents(true);
                ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                Content content = contentFactory.createContent(previewWindow.getContent(), "", false);
                toolWindow.getContentManager().addContent(content);
            });
        } finally {
            ThreadPoolUtil.execute(() -> refresh(project, toolWindow), 800);
        }
    }

}
