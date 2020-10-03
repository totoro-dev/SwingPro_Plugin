package top.totoro.swing.widget.layout;

import top.totoro.swing.widget.util.PaintUtil;
import top.totoro.swing.widget.util.SwingConstants;
import top.totoro.swing.widget.view.View;

import javax.swing.*;
import java.awt.*;

public class ToastContent extends LinearLayout {

    private JLabel content;
    private final Color backgroundColor = Color.decode("#d0d0d0");

    public ToastContent(View parent) {
        super(parent);
        component = new JPanel() {
            @Override
            public void paint(Graphics g) {
                content.setSize(getWidth() - 2, getHeight() - 2);
                PaintUtil.drawToastRadius(g, backgroundColor, getWidth(), getHeight());
                content.paint(g);
            }
        };
        component.setOpaque(false);
        component.setLayout(null);
    }

    public ToastContent(View parent, String text) {
        this(parent);
        setText(text);
    }

    public void setText(String text) {
        content = new JLabel(text, JLabel.CENTER);
        content.setBackground(backgroundColor);
        content.setLocation(1, 1);
        content.setFont(SwingConstants.TOAST_FONT);
        component.add(content);
    }

}
