package top.totoro.plugin.file;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileContent {

    /**
     * 获取文件的全部内容，按行分
     *
     * @param file 具体目标文件
     * @return 全部文件内容，如果没有就返回默认内容
     */
    public static String getFileContent(File file) {
        StringBuilder content = new StringBuilder();
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            String line = "";
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * 将最终的文件内容写入文件中
     *
     * @param file    具体文件
     * @param content 文件内容
     */
    public static void setFileContent(File file, String content) {
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            osw.write(content);
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
