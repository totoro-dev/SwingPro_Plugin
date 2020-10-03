package top.totoro.plugin.file;

import top.totoro.plugin.util.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static top.totoro.plugin.constant.Constants.*;

public class SwingProjectInitializer {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createSwingProjectFiles(String projectPath) {
        OutputStreamWriter osw = null;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            /************** 生成资源文件 *****************/
            String resPath = projectPath + "/src/main/resources";
            File resDir = new File(resPath);
            Log.d(SwingProjectInitializer.class, "resPath : " + resPath);
            if (!resDir.exists()) {
                Log.d(SwingProjectInitializer.class, "! resDir.exists()");
                resDir.mkdirs();
            }
            File layoutDir = new File(resDir + "/layout");
            File mipmapDir = new File(resDir + "/mipmap");
            File valuesDir = new File(resDir + "/values");
            File main_activity = new File(layoutDir.getPath() + "/activity_main.swing");
            File styles = new File(valuesDir.getPath() + "/styles.swing");
            layoutDir.mkdirs();
            mipmapDir.mkdirs();
            valuesDir.mkdirs();
            // 生成示例布局activity_main.swing文件
            main_activity.createNewFile();
            osw = new OutputStreamWriter(new FileOutputStream(main_activity), StandardCharsets.UTF_8);
            osw.write(DEFAULT_SWING_FILE_CONTENT);
            osw.flush();
            // 生成styles.swing文件
            styles.createNewFile();
            osw = new OutputStreamWriter(new FileOutputStream(styles), StandardCharsets.UTF_8);
            osw.write(DEFAULT_STYLES_FILE_CONTENT);
            osw.flush();
            /************** 生成Java文件 *****************/
            String javaPath = projectPath + "/src/main/java";
            File javaDir = new File(javaPath + "/ui");
            Log.d(SwingProjectInitializer.class, "javaPath : " + javaPath);
            if (!javaDir.exists()) {
                Log.d(SwingProjectInitializer.class, "! javaDir.exists()");
                javaDir.mkdirs();
            }
            File MainActivity = new File(javaPath + "/ui/MainActivity.java");
            MainActivity.createNewFile();
            osw = new OutputStreamWriter(new FileOutputStream(MainActivity), StandardCharsets.UTF_8);
            osw.write(DEFAULT_MAIN_ACTIVITY_CONTENT);
            osw.flush();
            SwingResGroupCreator.createResGroup(projectPath, main_activity, DEFAULT_SWING_FILE_CONTENT);
            /************** 添加SwingPro的依赖 *****************/
            File pomFile = new File(projectPath + "/pom.xml");
            String pomContent = "";
            StringBuilder content = new StringBuilder();
            fr = new FileReader(pomFile);
            br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
            content.insert(content.lastIndexOf("</project>"), DEPENDENCY);
            pomContent = content.toString();
            osw = new OutputStreamWriter(new FileOutputStream(pomFile), StandardCharsets.UTF_8);
            osw.write(pomContent);
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
