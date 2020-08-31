package top.totoro.plugin.core;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.totoro.plugin.file.Log;
import top.totoro.plugin.file.SwingResGroupCreator;
import top.totoro.plugin.util.ThreadPoolUtil;

import javax.swing.*;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleFileType extends LanguageFileType {
    private static final String TAG = SimpleFileType.class.getSimpleName();

    public static final SimpleFileType INSTANCE = new SimpleFileType();
    // 解决修改文件时频繁同步更新的问题，提升性能
    private final Map<VirtualFile, Integer> updateCounts = new ConcurrentHashMap<>();

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
            int updateCount = updateCounts.computeIfAbsent(virtualFile, key -> 1);
            updateCounts.put(virtualFile, updateCounts.get(virtualFile) + 1);
            ThreadPoolUtil.execute(() -> {
                if (updateCount + 1 != updateCounts.get(virtualFile)) return;
                String swingFilePath = virtualFile.getPath();
                Log.d(TAG, "swingFilePath : " + swingFilePath);
                // 当前处理的资源文件可能是主项目的，也可能是子模块的，需要做定位，决定了R.java文件的准确性
                String modulePath = swingFilePath.substring(0, swingFilePath.lastIndexOf("src/main"));
                // 开始创建或更新资源组，同步修改到R.java中
                SwingResGroupCreator.createResGroup(modulePath, new File(swingFilePath), content.toString());
                // 刷新项目，确保修改能第一时间被感应
                VirtualFile srcFile = virtualFile.getParent();
                while (srcFile != null && !srcFile.getPath().endsWith("src")) {
                    srcFile = srcFile.getParent();
                }
                Objects.requireNonNull(srcFile).refresh(true, true);
            }, 1000 /* 一秒内的所有修改只同步最后一次的修改 */);
        }
        return super.extractCharsetFromFileContent(project, virtualFile, content);
    }

}