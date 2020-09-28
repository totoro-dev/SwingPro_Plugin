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

    private List<String> containProjectPathList = new ArrayList<>();

    public static void setProject(Project project) {
        if (projects.get(project.getBasePath()) == null) {
            SwingProjectInfo swingProjectInfo = new SwingProjectInfo();
            swingProjectInfo.project = project;
            projects.put(project.getBasePath(), swingProjectInfo);
            swingProjectInfo.initSubProjectPath();
            // 初始化自定义View标签
            swingProjectInfo.initCustomViewTag();
            // 间断性刷新R.java
            ThreadPoolUtil.execute(swingProjectInfo::scanResPackage, 1000);
//            swingProjectInfo.scanResPackage();
        }
    }

    public void initSubProjectPath() {
        String projectPath = project.getBasePath();
        if (projectPath == null) return;
        containProjectPathList.add(projectPath.replace("\\", "/"));
        File rootPath = new File(projectPath);
        File[] files = rootPath.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.isDirectory()
                    || file.getName().equals("out")
                    || file.getName().equals("target")
                    || file.getName().equals("build"))
                continue;

            // TODO: 一般开发的子项目都只会在第二层路径，暂时不做更深的探寻
            searchProjectDeep(file);
        }
        Log.d(this, "containProjectPathList = " + containProjectPathList);
    }

    /**
     * 深度搜索子项目
     *
     * @param parentProject 父项目路径
     */
    private void searchProjectDeep(File parentProject) {
        File[] files = parentProject.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.isDirectory()
                    || file.getName().equals("out")
                    || file.getName().equals("target")
                    || file.getName().equals("build"))
                continue;

            // 不是主项目下的src文件夹，说明这是一个子项目的src文件夹
            if (file.getName().equals("src")) {
                String parentPath = file.getParent().replace("\\", "/");
                if (containProjectPathList.contains(parentPath)) continue;
                containProjectPathList.add(parentPath);
            }
        }
    }

    public void initCustomViewTag() {
        for (String projectPath : containProjectPathList) {
            String srcPath = projectPath + "/src/main/java";
            File srcDir = new File(srcPath);
            if (srcDir.exists() && srcDir.isDirectory()) {
                for (File file : Objects.requireNonNull(srcDir.listFiles())) {
                    scanJavaPackage(file);
                }
            }
            resolveOtherTag();
        }
        // 10秒扫描一次
        ThreadPoolUtil.execute(this::initCustomViewTag, 10000);
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

    /**
     * 扫描项目中的图片资源包、布局资源包，添加记录到R.java中
     */
    public void scanResPackage() {
        for (String projectPath : containProjectPathList) {
            SwingResGroupCreator.createMipmapGroup(projectPath);
            SwingResGroupCreator.createIdGroup(projectPath);
        }
        // 及时同步刷新项目，使得修改后的引用能被索引到
        project.getBaseDir().refresh(false, true);
        // 10秒扫描一次
        ThreadPoolUtil.execute(this::scanResPackage, 10000);
    }

    /**
     * 扫描项目中的源码包，记录创建的自定义View
     *
     * @param parent 扫描的包目录
     */
    private void scanJavaPackage(File parent) {
        Log.d(this, "scanJavaPackage() path = " + parent.getPath());
        for (File file : Objects.requireNonNull(parent.listFiles())) {
            if (file.isDirectory()) {
                scanJavaPackage(file);
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
