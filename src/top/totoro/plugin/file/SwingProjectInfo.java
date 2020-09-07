package top.totoro.plugin.file;

import com.intellij.openapi.project.Project;
import top.totoro.plugin.core.SimpleCompletionContributor;
import top.totoro.plugin.util.ThreadPoolUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SwingProjectInfo {
    private Project project;
    private final Map<String, String> waitToResolveTags = new HashMap<>();
    private final List<String> hasInitFiles = new LinkedList<>();
    private static final Map<String, SwingProjectInfo> projects = new ConcurrentHashMap<>();

    public static void setProject(Project project) {
        if (projects.get(project.getBasePath()) == null) {
            SwingProjectInfo swingProjectInfo = new SwingProjectInfo();
            swingProjectInfo.project = project;
            projects.put(project.getBasePath(), swingProjectInfo);
            swingProjectInfo.initCustomViewTag();
        }
    }

    public void initCustomViewTag() {
        String projectPath = project.getBasePath();
        String srcPath = projectPath + "/src/main/java";
        File srcDir = new File(srcPath);
        if (srcDir.exists() && srcDir.isDirectory()) {
            for (File file : Objects.requireNonNull(srcDir.listFiles())) {
                scanPackage(file);
            }
        }
        resolveOtherTag();
        ThreadPoolUtil.execute(this::initCustomViewTag, 2000);
    }

    private void resolveOtherTag() {
        waitToResolveTags.forEach((tagName, extendsClass) -> {
            for (String layoutTag : SimpleCompletionContributor.layoutTags) {
                if (layoutTag.endsWith(extendsClass)) {
                    SimpleCompletionContributor.addCustomLayoutTagLookElement(tagName);
                    return;
                }
            }
            for (String viewTag : SimpleCompletionContributor.viewTags) {
                if (viewTag.endsWith(extendsClass)) {
                    SimpleCompletionContributor.addCustomViewTagLookElement(tagName);
                    return;
                }
            }
        });
        waitToResolveTags.clear();
    }

    private void scanPackage(File parent) {
        Log.d(this, "scanPackage() path = " + parent.getPath());
        for (File file : Objects.requireNonNull(parent.listFiles())) {
            if (file.isDirectory()) {
                scanPackage(file);
                continue;
            }
            if (hasInitFiles.contains(file.getPath())) {
                continue;
            }
            hasInitFiles.add(file.getPath());
            String fileContent = FileContent.getFileContent(file);
            int extendsClassIndex = fileContent.indexOf(" extends ");
            if (extendsClassIndex > 0) {
                extendsClassIndex += " extends ".length();
                fileContent = fileContent.substring(extendsClassIndex);
                String extendClass = fileContent.substring(0, fileContent.indexOf("{")).trim();
                String filePath = file.getPath().replaceAll("[/\\\\]", ".");
                String tagName = filePath.substring(filePath.lastIndexOf(".java.") + ".java.".length(), filePath.lastIndexOf("."));
                Log.d(this, "extendClass = " + extendClass);
                if (SimpleCompletionContributor.layoutTags.contains(extendClass)) {
                    SimpleCompletionContributor.addCustomLayoutTagLookElement(tagName);
                } else if (extendClass.equals("BaseLayout")) {
                    SimpleCompletionContributor.addCustomLayoutTagLookElement(tagName);
                } else if (SimpleCompletionContributor.viewTags.contains(extendClass)) {
                    SimpleCompletionContributor.addCustomViewTagLookElement(tagName);
                } else if (extendClass.startsWith("View<")) {
                    SimpleCompletionContributor.addCustomViewTagLookElement(tagName);
                } else {
                    waitToResolveTags.put(tagName, extendClass);
                }
            }
        }
    }

    public static SwingProjectInfo getSwingProject(String filePath) {
        Set<String> projectKeys = projects.keySet();
        String projectFilePath = "";
        for (String projectKey : projectKeys) {
            if (filePath.contains(projectKey)) {
                if (Math.max(projectFilePath.length(), projectKey.length()) > projectFilePath.length()) {
                    projectFilePath = projectKey;
                }
            }
        }
        return projects.get(projectFilePath);
    }

    public Project getProject() {
        return project;
    }
}
