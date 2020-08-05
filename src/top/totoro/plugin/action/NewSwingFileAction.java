package top.totoro.plugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static top.totoro.plugin.constant.Constants.*;

public class NewSwingFileAction extends AnAction {

    private Project project;
    private String path;
    private VirtualFile chooseFile;

    public NewSwingFileAction() {
        super("新建Swing布局");
    }

    public void setChooseFile(VirtualFile chooseFile) {
        this.chooseFile = chooseFile;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        project = anActionEvent.getProject();
        new NewSwingFileDialog().show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setEnabled(false);
        /* 处理新建Swing布局按钮是否可用 */
        if (chooseFile.getPath().contains("resources")) {
            if (chooseFile.getName().equals("resources") || chooseFile.getName().equals("layout")) {
                e.getPresentation().setEnabled(true);
            }
        }
    }

    public void setPath(String path) {
        this.path = path;
    }


    /* 点击新建Swing布局的对话框 */
    public class NewSwingFileDialog extends DialogWrapper {

        public NewSwingFileDialog() {
            super(true);
            setTitle("新建Swing布局文件"); //设置会话框标题
            setResizable(false);
            init(); //触发一下init方法，否则swing样式将无法展示在会话框
        }


        private final JTextField nameContent = new JTextField(30);

        public JPanel initNorth() {
            //定义表单的标题部分，放置到IDEA会话框的顶部位置
            return null;
        }

        public JPanel initCenter() {
            JPanel center = new JPanel();
            JLabel name = new JLabel("文件名：");
            //定义表单的主体部分，放置到IDEA会话框的中央位置
            center.setLayout(new BorderLayout());

            //row1：文件名+文本框
            center.add(name, BorderLayout.WEST);
            center.add(nameContent, BorderLayout.CENTER);
            return center;
        }

        public JPanel initSouth() {
            JPanel south = new JPanel();
            //定义表单的提交按钮，放置到IDEA会话框的底部位置
            JButton submit = new JButton("创建");
            submit.setHorizontalAlignment(SwingConstants.CENTER); //水平居中
            submit.setVerticalAlignment(SwingConstants.CENTER); //垂直居中
            south.add(submit);

            //按钮事件绑定
            submit.addActionListener(e -> {
                createSwingFile();
            });

            nameContent.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER)
                        createSwingFile();
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });

            return south;
        }

        @SuppressWarnings({"DialogTitleCapitalization", "ResultOfMethodCallIgnored"})
        private void createSwingFile() {
            if (nameContent.getText() == null || nameContent.getText().isEmpty()){
                Messages.showMessageDialog(project, "文件名不能为空", "无法创建", Messages.getErrorIcon());
                return;
            }
            String filename = nameContent.getText() + ".swing";
            File swingFile = new File(path.endsWith("layout") ? path + "/" + filename : path + "/layout/" + filename);
            if (swingFile.exists()) {
                Messages.showMessageDialog(project, filename + "已存在", "无法创建", Messages.getErrorIcon());
                return;
            }
            if (!swingFile.getParentFile().exists()) {
                swingFile.getParentFile().mkdirs();
            }
            if (swingFile.getParentFile().exists()) {
                try {
                    swingFile.createNewFile();
                    OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(swingFile), StandardCharsets.UTF_8);
                    osw.write(NEW_SWING_FILE_CONTENT);
                    osw.flush();
                    osw.close();
                    // 刷新目录
                    chooseFile.refresh(false, true);
                    close(1);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

        @Override
        protected JComponent createNorthPanel() {
            return initNorth(); //返回位于会话框north位置的swing样式
        }

        // 特别说明：不需要展示SouthPanel要重写返回null，否则IDEA将展示默认的"Cancel"和"OK"按钮
        @Override
        protected JComponent createSouthPanel() {
            return initSouth();
        }

        @Override
        protected JComponent createCenterPanel() {
            //定义表单的主题，放置到IDEA会话框的中央位置
            return initCenter();
        }
    }

}
