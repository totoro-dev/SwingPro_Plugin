package top.totoro.plugin.file;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private static final String DEFAULT_R_FILE_CONTENT = "package swing;\n\npublic class R{\n\n}";

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

    private static final Map<File, Map<File, List<String>>> idInRFilesMap = new ConcurrentHashMap<>();
    // key: 项目或子项目的路径， value: 项目路径下的资源文件集合
    private static final Map<String, List<File>> resFilesMap = new ConcurrentHashMap<>();

    public static void createResGroup(String projectPath, File res, String content) {
        File resParent = res.getParentFile();
        if (resParent == null) return;
        String resParentName = resParent.getName();
        switch (resParentName) {
            case "layout":
                List<File> resFiles = resFilesMap.computeIfAbsent(projectPath, key -> new ArrayList<>());
                if (!resFiles.contains(res)) {
                    resFiles.add(res);
                    createLayoutGroup(projectPath, res);
                }
                createIdGroup(projectPath, res, content);
                break;
            case "values":
                break;
        }
    }

    private static void createLayoutGroup(String projectPath, File res) {
        try {
            // 确定R.java文件已生成
            File RFile = checkRFileCreated(projectPath);
            if (RFile.exists()) {
                String originRFileContents = getRFileContent(RFile);
                StringBuilder finalContents = new StringBuilder(originRFileContents);
                List<File> layoutFiles = resFilesMap.computeIfAbsent(projectPath, key -> new ArrayList<>());
                String[] totals = originRFileContents.split(layoutClassStartRegex);
                if (totals.length == 1) {
                    finalContents = new StringBuilder(totals[0].substring(0, totals[0].lastIndexOf("}")));
                    finalContents.append(layoutClassStart);
                    StringBuilder finalContents1 = finalContents;
                    for (File layoutFile : layoutFiles) {
                        String fileName = layoutFile.getName();
                        finalContents1.append(layoutFieldStart)
                                .append(fileName, 0, fileName.lastIndexOf("."))
                                .append("=\"").append(fileName).append("\";\n");
                    }
                    finalContents1.append("\t}\n}");
                    finalContents = finalContents1;
                } else if (totals.length == 2) {
                    finalContents = new StringBuilder(totals[0]);
                    String anotherContent = totals[1].substring(totals[1].indexOf("}\n") + 2);
                    finalContents.append(layoutClassStart);
                    StringBuilder finalContents1 = finalContents;
                    for (File layoutFile : layoutFiles) {
                        String fileName = layoutFile.getName();
                        finalContents1.append(layoutFieldStart)
                                .append(fileName, 0, fileName.lastIndexOf("."))
                                .append("=\"").append(fileName).append("\";\n");
                    }
                    finalContents1.append("\t}\n");
                    finalContents = finalContents1.append(anotherContent);
                }
                if (finalContents.toString().equals(originRFileContents)) return;
                setRFileContent(RFile, finalContents.toString());
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public static void createIdGroup(String projectPath, File res, String resFileContent) {
        try {
            // 确定R.java文件已生成
            File RFile = checkRFileCreated(projectPath);
            if (RFile.exists()) {
                String[] ids = resFileContent.split("id *= *\"");
                String[] finalIds = new String[0];
                if (ids.length > 0) {
                    finalIds = new String[ids.length - 1];
                    for (int i = 1; i < ids.length; i++) {
                        finalIds[i - 1] = ids[i].substring(0, ids[i].indexOf("\""));
                    }
                }
                String originRFileContents = getRFileContent(RFile);
                StringBuilder finalContents = new StringBuilder(originRFileContents);
                Map<File, List<String>> idInFileMap = idInRFilesMap.computeIfAbsent(RFile, key -> new ConcurrentHashMap<>());
                List<String> originFileIds = new ArrayList<>(Arrays.asList(finalIds));
                idInFileMap.put(res, originFileIds.stream().distinct().collect(Collectors.toList()));
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
                    idInFileMap.forEach((file, idList) -> {
                        for (String id : idList) {
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
            // 没有内容，返回初始化R.java
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
        try (FileWriter fileWriter = new FileWriter(RFile, false)) {
            fileWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
