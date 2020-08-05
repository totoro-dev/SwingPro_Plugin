package top.totoro.plugin.ui;

import com.intellij.ide.projectWizard.NewProjectWizard;
import com.intellij.ide.projectWizard.ProjectTypeStep;
import com.intellij.ide.util.newProjectWizard.TemplatesGroup;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.internal.statistic.eventLog.FeatureUsageUiEventsKt;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.ProjectTemplate;
import com.intellij.remoteServer.impl.module.CloudModuleBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.totoro.plugin.file.Log;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NewSwingModuleWrapper extends NewProjectWizard {

    private String newModuleName = "";
    private VirtualFile rootFile;

    public NewSwingModuleWrapper(@Nullable Project project, @NotNull ModulesProvider modulesProvider, @Nullable String defaultPath) {
        super(project, modulesProvider, defaultPath);
//        myWizardContext.setProjectBuilder(new JavaModuleBuilder());
    }

    public void setRootFile(VirtualFile rootFile) {
        this.rootFile = rootFile;
    }

    /**
     * 覆盖原来的显示面板，通过okAction()来触发创建项目
     */
    public boolean showAndGet() {
        return super.showAndGet();
//        if (!isModal()) {
//            throw new IllegalStateException("The showAndGet() method is for modal dialogs only");
//        }
//        final boolean canRecord = canRecordDialogId();
//        if (canRecord) {
//            final String dialogId = getClass().getName();
//            if (StringUtil.isNotEmpty(dialogId)) {
//                FeatureUsageUiEventsKt.getUiEventLogger().logShowDialog(dialogId, getClass());
//            }
//        }
//
//        Disposable uiParent = Disposer.get("ui");
//        if (uiParent != null) { // may be null if no app yet (license agreement)
//            Disposer.register(uiParent, myDisposable); // ensure everything is disposed on app quit
//        }
//        Log.d(this, "showAndGet() start");
//        new NewSwingModuleDialog().show();
//
//        return isOK();
    }

    @Override
    public void doNextAction() {
        Log.d(this, "doNextAction : " + mySteps.get(0).getClass());
        if (mySteps.get(0) instanceof ProjectTypeStep) {
            ProjectTypeStep step = (ProjectTypeStep) mySteps.get(0);
            try {
                Method method = step.getClass().getDeclaredMethod("getSelectedGroup");
                method.setAccessible(true);
                TemplatesGroup group = (TemplatesGroup) method.invoke(step);
                Log.d(this, "getSelectedGroup : " + group);
                if (group.getName().isEmpty() || !group.getName().equals("Maven")) {
                    Messages.showErrorDialog("必须选择Maven工程", "工程类型错误");
                    return;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        super.doNextAction();
    }

    @Override
    public boolean doFinishAction() {
        return super.doFinishAction();
    }

    /* 点击新建Swing模块的对话框 */
    public class NewSwingModuleDialog extends DialogWrapper {

        public NewSwingModuleDialog() {
            super(true);
            setTitle("创建Swing模块"); //设置会话框标题
            setResizable(false);
            init(); //触发一下init方法，否则swing样式将无法展示在会话框
        }

        private final JPanel center = new JPanel();

        private final JPanel south = new JPanel();

        private final JLabel name = new JLabel("模块名：");
        private final JTextField nameContent = new JTextField(30);

        public JPanel initNorth() {
            //定义表单的标题部分，放置到IDEA会话框的顶部位置
            return null;
        }

        public JPanel initCenter() {
            //定义表单的主体部分，放置到IDEA会话框的中央位置
            center.setLayout(new BorderLayout());

            //row1：模块名+文本框
            center.add(name, BorderLayout.WEST);
            center.add(nameContent, BorderLayout.CENTER);
            return center;
        }

        public JPanel initSouth() {
            //定义表单的提交按钮，放置到IDEA会话框的底部位置
            JButton submit = new JButton("创建");
            submit.setHorizontalAlignment(SwingConstants.CENTER); //水平居中
            submit.setVerticalAlignment(SwingConstants.CENTER); //垂直居中
            south.add(submit);

            //按钮事件绑定
            submit.addActionListener(e -> {
                newModuleName = nameContent.getText();
                if (newModuleName.isEmpty()) {
                    Messages.showErrorDialog("模块名不能为空", "无法创建");
                }
                String rootPath = rootFile.getPath();
                File root = new File(rootPath);
                File[] children = root.listFiles();
                if (children != null) {
                    for (File child : children) {
                        if (child.getName().equals(newModuleName)) {
                            Messages.showErrorDialog("模块名已存在", "无法创建");
                            break;
                        }
                    }
                }
                myWizardContext.setProjectName(newModuleName);
                doOKAction();
                close(1);
            });

            return south;
        }

        @Override
        protected JComponent createNorthPanel() {
            return initNorth(); //返回位于会话框north位置的swing样式
        }

        // 特别说明：不需要展示SouthPanel要重写返回null，否则IDEA将展示默认的"Cancel"和"OK"按钮
        @Override
        protected JComponent createSouthPanel() {
            return initSouth();
        }

        @Override
        protected JComponent createCenterPanel() {
            //定义表单的主题，放置到IDEA会话框的中央位置
            return initCenter();
        }
    }
}
