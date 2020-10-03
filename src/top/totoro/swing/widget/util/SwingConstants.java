package top.totoro.swing.widget.util;

import java.awt.*;

public class SwingConstants {

    public static final Font TOAST_FONT = new Font(Font.SERIF, Font.PLAIN, 14);

    public static final Font TEXTVIEW_FONT = new Font(Font.SERIF, Font.PLAIN, 16);

    public static Dimension getScreenSize() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle rect = ge.getMaximumWindowBounds(); // 不包含任务栏的全屏大小
        return new Dimension(rect.width, rect.height);
    }

}
