package top.totoro.plugin.ui;

import top.totoro.plugin.core.SimpleFileType;
import top.totoro.swing.widget.base.Size;
import top.totoro.swing.widget.context.Activity;
import top.totoro.swing.widget.view.EditText;

import javax.swing.*;
import java.awt.*;

/**
 * 创建时间 2020/10/3 15:34
 *
 * @author dragon
 * @version 1.0
 */
public class PreviewWindow {
    private static int width = 500, height = 500;
    private final Activity activity;
    private final JPanel container;

    static {
        EditText.isPlugin = true;
    }

    public PreviewWindow() {
        activity = Activity.newInstance(new Size(width, height));
        container = new JPanel(new BorderLayout());
    }

    public void createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel tabLabel = new JLabel("窗口设置:  ");
        JLabel widthLabel = new JLabel("宽度");
        JLabel heightLabel = new JLabel("高度");
        JTextField widthEdit = new JTextField(width + "", 5);
        JTextField heightEdit = new JTextField(height + "", 5);
        JButton update = new JButton("确定");
        topBar.add(tabLabel);
        topBar.add(widthLabel);
        topBar.add(widthEdit);
        topBar.add(heightLabel);
        topBar.add(heightEdit);
        topBar.add(update);
        update.addActionListener(e -> {
            width = Integer.parseInt(widthEdit.getText().isEmpty() ? width + "" : widthEdit.getText());
            height = Integer.parseInt(heightEdit.getText().isEmpty() ? height + "" : heightEdit.getText());
            SimpleFileType.updated = true;
        });
        container.add(topBar, BorderLayout.NORTH);
    }

    public void setResPath(String resPath) {
        createTopBar();
        activity.setContentViewByAbsolute(resPath);
        container.add(activity.getMainView().getComponent(), BorderLayout.CENTER);
    }

    public JPanel getContent() {
        return container;
    }

}
