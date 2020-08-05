package top.totoro.plugin.test;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.totoro.plugin.file.Log;
import top.totoro.plugin.file.SwingProjectInfo;
import top.totoro.plugin.file.SwingResGroupCreator;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class SimpleFileType extends LanguageFileType {
    private static final String TAG = SimpleFileType.class.getSimpleName();

    public static final SimpleFileType INSTANCE = new SimpleFileType();

    private SimpleFileType() {
        super(SimpleLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Swing File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Swing language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "swing";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SimpleIcons.FILE;
    }

    @Override
    public String getCharset(@NotNull VirtualFile file, @NotNull byte[] content) {
        if (file.getPath().contains("src/main/resources")) {
            if (!Objects.equals(file.getExtension(), getDefaultExtension())) return StandardCharsets.UTF_8.name();
        }
        return super.getCharset(file, content);
    }

    @Override
    public Charset extractCharsetFromFileContent(@Nullable Project project, @Nullable VirtualFile virtualFile, @NotNull CharSequence content) {
        Log.d(TAG, "extractCharsetFromFileContent()");
        if (project == null || virtualFile == null) return super.extractCharsetFromFileContent(project, null, content);
        if (Objects.equals(virtualFile.getExtension(), getDefaultExtension()) && virtualFile.getPath().contains("src/main/resources")) {
            String swingFilePath = virtualFile.getPath();
            Log.d(TAG, "swingFilePath : " + swingFilePath);
            if (swingFilePath.lastIndexOf("src/main") > 0) {
                // 当前处理的资源文件可能是主项目的，也可能是子模块的，需要做定位，决定了R.java文件的准确性
                String modulePath = swingFilePath.substring(0, swingFilePath.lastIndexOf("src/main"));
                Log.d(TAG, "project : " + modulePath);
                // 开始创建或更新资源组，同步修改到R.java中
                SwingResGroupCreator.createResGroup(modulePath, new File(swingFilePath), content.toString());
                // 刷新项目，确保修改能第一时间被感应
//                try {
//                    SwingUtilities.invokeAndWait(()->{
//                        project.getBaseDir().refresh(false, true);
//                    });
//                } catch (InterruptedException | InvocationTargetException e) {
//                    e.printStackTrace();
//                }
            }
        }
        return super.extractCharsetFromFileContent(project, virtualFile, content);
    }

}