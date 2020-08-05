package top.totoro.plugin.file;

import com.intellij.openapi.project.Project;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SwingProjectInfo {
    private Project project;
    private static final Map<String, SwingProjectInfo> projects = new ConcurrentHashMap<>();

    public static void setProject(Project project) {
        if (projects.get(project.getBasePath()) == null) {
            SwingProjectInfo swingProjectInfo = new SwingProjectInfo();
            swingProjectInfo.project = project;
            projects.put(project.getBasePath(), swingProjectInfo);
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
