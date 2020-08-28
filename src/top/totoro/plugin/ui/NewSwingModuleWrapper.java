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


    public NewSwingModuleWrapper(@Nullable Project project, @NotNull ModulesProvider modulesProvider, @Nullable String defaultPath) {
        super(project, modulesProvider, defaultPath);
    }

    /**
     * 覆盖原来的显示面板，通过okAction()来触发创建项目
     */
    public boolean showAndGet() {
        return super.showAndGet();
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

}
