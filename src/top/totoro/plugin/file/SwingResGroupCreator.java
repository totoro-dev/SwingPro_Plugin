package top.totoro.plugin.file;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.messages.MessagesService;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.EventDispatcher;
import top.totoro.plugin.util.ThreadPoolUtil;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;
import static top.totoro.plugin.constant.Constants.DEFAULT_R_FILE_CONTENT;

/**
 * Swing中所有资源文件的整合
 */
public class SwingResGroupCreator {
    private static final String TAG = SwingResGroupCreator.class.getSimpleName();
    private static final String RFilePath = File.separator + "src" +
            File.separator + "main" +
            File.separator + "java" +
            File.separator + "swing" +
            File.separator + "R.java";

    private static final String idClassStart = "\tpublic static class id {\n";
    private static final String idClassStartRegex = "\tpublic static class id \\{\n";
    private static final String idFieldStart = "\t\tpublic static final String ";
    private static final String layoutClassStart = "\tpublic static class layout {\n";
    private static final String layoutClassStartRegex = "\tpublic static class layout \\{\n";
    private static final String layoutFieldStart = "\t\tpublic static final String ";
    private static final String stringClassStart = "\tpublic static class string {\n";
    private static final String stringClassStartRegex = "\tpublic static class string \\{\n";
    private static final String stringFieldStart = "\t\tpublic static final String ";
    private static final String colorClassStart = "\tpublic static class color {\n";
    private static final String colorClassStartRegex = "\tpublic static class color \\{\n";
    private static final String colorFieldStart = "\t\tpublic static final Color ";
    private static final String mipmapClassStart = "\tpublic static class mipmap {\n";
    private static final String mipmapClassStartRegex = "\tpublic static class mipmap \\{\n";
    private static final String mipmapFieldStart = "\t\tpublic static final String ";

    private static final Map<File, Map<File, List<String>>> idInRFilesMap = new ConcurrentHashMap<>();
    // 各个资源文件下冲突的id表
    private static final Map<File, List<String>> multipleIds = new ConcurrentHashMap<>();
    // key: 项目或子项目的路径， value: 项目路径下的资源文件集合
    private static final Map<String, List<File>> resFilesMap = new ConcurrentHashMap<>();

    public static void createResGroup(String projectPath, File res, String content) {
        File resParent = res.getParentFile();
        if (resParent == null) return;
        String resParentName = resParent.getName();
        Log.d(TAG, "resParentName = " + resParentName);
        switch (resParentName) {
            case "layout":
                Log.d(TAG, "createResGroup() is layout");
                List<File> resFiles = resFilesMap.computeIfAbsent(projectPath, key -> new ArrayList<>());
                File[] layoutFiles = resParent.listFiles();
                if (layoutFiles == null) break;
                resFiles.clear();
                resFiles.addAll(Arrays.asList(layoutFiles));
                createLayoutGroup(projectPath, resFiles);
                createIdGroup(projectPath, res, content);
                break;
            case "values":
                break;
        }
    }

    // 需要在项目打开的时候间断性刷新
    public static void createMipmapGroup(String projectPath) {
        String resPath = projectPath + "/src/main/resources/";
        // 得到mipmap目录
        String mipmapDirPath = resPath + "mipmap";
        File mipmapDir = new File(mipmapDirPath);
        File[] mipmapFiles = mipmapDir.listFiles();
        if (mipmapFiles == null) return;
        // 刷新R.java中的mipmap引用
        createMipmapGroup(projectPath, Arrays.asList(mipmapFiles));
    }

    private static void createMipmapGroup(String projectPath, List<File> mipmapFiles) {
        Log.d(TAG, "createMipmapGroup() mipmap file size = " + mipmapFiles.size());
        try {
            // 确定R.java文件已生成
            File RFile = checkRFileCreated(projectPath);
            if (RFile.exists()) {
                String originRFileContents = getRFileContent(RFile);
                StringBuilder finalContents = new StringBuilder(originRFileContents);
                String[] totals = originRFileContents.split(mipmapClassStartRegex);
                if (totals.length == 1) {
                    finalContents = new StringBuilder(totals[0].substring(0, totals[0].lastIndexOf("}")));
                    finalContents.append(mipmapClassStart);
                    for (File mipmapFile : mipmapFiles) {
                        String fileName = mipmapFile.getName();
                        String field = mipmapFieldStart + fileName.substring(0, fileName.indexOf(".")) + "=\"" + fileName + "\";\n";
                        finalContents.append(field);
                    }
                    finalContents.append("\t}\n}");
                } else if (totals.length == 2) {
                    finalContents = new StringBuilder(totals[0]);
                    String anotherContent = totals[1].substring(totals[1].indexOf("}\n") + 2);
                    finalContents.append(mipmapClassStart);
                    for (File mipmapFile : mipmapFiles) {
                        String fileName = mipmapFile.getName();
                        String field = mipmapFieldStart + fileName.substring(0, fileName.indexOf(".")) + "=\"" + fileName + "\";\n";
                        finalContents.append(field);
                    }
                    finalContents.append("\t}\n").append(anotherContent);
                }
                if (finalContents.toString().equals(originRFileContents)) return;
                setRFileContent(RFile, finalContents.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createLayoutGroup(String projectPath, List<File> layoutFiles) {
        Log.d(TAG, "createLayoutGroup() layout file size = " + layoutFiles.size());
        try {
            // 确定R.java文件已生成
            File RFile = checkRFileCreated(projectPath);
            if (RFile.exists()) {
                String originRFileContents = getRFileContent(RFile);
                StringBuilder finalContents = new StringBuilder(originRFileContents);
                String[] totals = originRFileContents.split(layoutClassStartRegex);
                if (totals.length == 1) {
                    finalContents = new StringBuilder(totals[0].substring(0, totals[0].lastIndexOf("}")));
                    finalContents.append(layoutClassStart);
                    for (File layoutFile : layoutFiles) {
                        String fileName = layoutFile.getName();
                        String field = layoutFieldStart + fileName.substring(0, fileName.lastIndexOf(".")) + "=\"" + fileName + "\";\n";
                        finalContents.append(field);
                    }
                    finalContents.append("\t}\n}");
                } else if (totals.length == 2) {
                    finalContents = new StringBuilder(totals[0]);
                    String anotherContent = totals[1].substring(totals[1].indexOf("}\n") + 2);
                    finalContents.append(layoutClassStart);
                    for (File layoutFile : layoutFiles) {
                        if (layoutFile == null) continue;
                        String fileName = layoutFile.getName();
                        String field = layoutFieldStart + fileName.substring(0, fileName.lastIndexOf(".")) + "=\"" + fileName + "\";\n";
                        finalContents.append(field);
                    }
                    finalContents.append("\t}\n").append(anotherContent);
                }
                if (finalContents.toString().equals(originRFileContents)) return;
                setRFileContent(RFile, finalContents.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createIdGroup(String projectPath) {
        String resPath = projectPath + "/src/main/resources/";
        // 得到layout目录
        String layoutDirPath = resPath + "layout";
        File layoutDir = new File(layoutDirPath);
        File[] layoutFiles = layoutDir.listFiles();
        if (layoutFiles == null) return;
        // 刷新R.java中的id引用
        for (File layoutFile : layoutFiles) {
            String content = FileContent.getFileContent(layoutFile);
            if (content.length() > 0) {
                createIdGroup(projectPath, layoutFile, content);
            }
        }
        createLayoutGroup(projectPath, Arrays.asList(layoutFiles));
    }

    public static void createIdGroup(String projectPath, File res, String resFileContent) {
        try {
            // 确定R.java文件已生成
            File RFile = checkRFileCreated(projectPath);
            if (RFile.exists()) {
                String[] ids = resFileContent.split("id *= *\"");
                // 提取所有的id值
                String[] finalIds = new String[0];
                if (ids.length > 0) {
                    finalIds = new String[ids.length - 1];
                    for (int i = 1; i < ids.length; i++) {
                        if (!ids[i].contains("\"")) continue;
                        finalIds[i - 1] = ids[i].substring(0, ids[i].indexOf("\""));
                    }
                }
                // R.java文件中原始的内容
                String originRFileContents = getRFileContent(RFile);
                // 最终的R.java文件的内容
                StringBuilder finalContents = new StringBuilder(originRFileContents);
                Map<File, List<String>> idInFileMap = idInRFilesMap.computeIfAbsent(RFile, key -> new ConcurrentHashMap<>());
                // 当前资源文件中的id值
                List<String> idsInResFile = new ArrayList<>();
                for (String finalId : finalIds) {
                    if (idsInResFile.contains(finalId) && !multipleIds.computeIfAbsent(res, key -> new ArrayList<>()).contains(finalId)) {
                        // 防止重复提示
                        multipleIds.get(res).add(finalId);
                        ThreadPoolUtil.execute(() -> {
                            // 冲突已经解决，不需要提示
                            if (!multipleIds.get(res).contains(finalId)) return;
                            ApplicationManager.getApplication().invokeLater(() -> {
                                Messages.showErrorDialog(finalId + "重复定义", "ID错误");
                            }, ModalityState.NON_MODAL);
                        }, 100);
                    } else {
                        // 空的id不能被使用
                        if ("".equals(finalId)) continue;
                        idsInResFile.add(finalId);
                    }
                }
                // 更新冲突的id表
                List<String> multipleIdsInRes = new ArrayList<>(multipleIds.computeIfAbsent(res, key -> new ArrayList<>()));
                for (String multipleId : multipleIds.computeIfAbsent(res, key -> new ArrayList<>())) {
                    int matchCount = 0;
                    for (String finalId : finalIds) {
                        if (finalId.equals(multipleId)) matchCount++;
                    }
                    if (matchCount < 2) multipleIdsInRes.remove(multipleId);
                }
                multipleIds.put(res, multipleIdsInRes);
                idInFileMap.put(res, idsInResFile);
                // 处理加入的id
                String[] totals = originRFileContents.split(idClassStartRegex);
                if (totals.length == 1) {
                    finalContents = new StringBuilder(totals[0].substring(0, totals[0].lastIndexOf("}")));
                    finalContents.append(idClassStart);
                    StringBuilder finalContents1 = finalContents;
                    idInFileMap.forEach((file, idList) -> {
                        for (String id : idList) {
                            finalContents1.append(idFieldStart)
                                    .append(id).append("=\"")
                                    .append(id).append("\";\n");
                        }
                    });
                    finalContents1.append("\t}\n}");
                    finalContents = finalContents1;
                } else if (totals.length == 2) {
                    finalContents = new StringBuilder(totals[0]);
                    finalContents.append(idClassStart);
                    String anotherContent = totals[1].substring(totals[1].indexOf("}\n") + 2);
                    StringBuilder finalContents1 = finalContents;
                    List<String> distinctIds = new ArrayList<>();
                    idInFileMap.forEach((file, idList) -> {
                        for (String id : idList) {
                            if (distinctIds.contains(id)) continue;
                            distinctIds.add(id);
                            finalContents1.append(idFieldStart)
                                    .append(id).append("=\"")
                                    .append(id).append("\";\n");
                        }
                    });
                    finalContents1.append("\t}\n");
                    finalContents = finalContents1.append(anotherContent);
                }
                if (finalContents.toString().equals(originRFileContents)) return;
                setRFileContent(RFile, finalContents.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkFileHasLoad(String projectPath, String filePath) {
        File projectFile = new File(projectPath + RFilePath);
        File file = new File(filePath);
        return idInRFilesMap.get(projectFile) != null
                && idInRFilesMap.get(projectFile).get(file) != null;
    }

    private static File checkRFileCreated(String projectPath) throws IOException {
        File RFile = new File(projectPath + RFilePath);
        // 确定R.java文件已生成
        if (!RFile.getParentFile().exists()) {
            boolean mkdirs = RFile.getParentFile().mkdirs();
            if (mkdirs) {
                if (!RFile.exists()) {
                    Log.d(TAG, "create R file " + (RFile.createNewFile() ? "success" : "false"));
                    setRFileContent(RFile, DEFAULT_R_FILE_CONTENT);
                }
            } else {
                Log.d(TAG, "mkdirs " + RFile.getParentFile().getPath() + " false");
            }
        }
        return RFile;
    }

    /**
     * 获取R.java文件的全部内容，按行分
     *
     * @param RFile R.java的具体目标文件
     * @return 全部文件内容，如果没有就返回默认内容
     */
    private static String getRFileContent(File RFile) {
        StringBuilder content = new StringBuilder();
        try (FileReader fr = new FileReader(RFile); BufferedReader br = new BufferedReader(fr)) {
            String line = "";
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (content.length() == 0) {
            // 没有内容，返回初始化R.java// 系统自动生成的代码，请不要做任何修改
            return DEFAULT_R_FILE_CONTENT;
        }
        return content.toString();
    }

    /**
     * 将最终的R.java文件内容写入文件中
     *
     * @param RFile   R.java的具体文件
     * @param content 文件内容
     */
    private static void setRFileContent(File RFile, String content) {
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(RFile), StandardCharsets.UTF_8)) {
            osw.write(content);
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
