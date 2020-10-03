package top.totoro.swing.widget.view;

import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.util.PaintUtil;

import javax.swing.*;
import java.awt.*;

/**
 * 普通按钮
 */
public class Button extends TextView {

    private Color bg = Color.gray;

    public Button(View parent) {
        super(parent);
        component = new JLabel("", JLabel.CENTER) {
            @Override
            public void paint(Graphics g) {
                top.totoro.swing.widget.view.Button.this.paintBorder(g); // 绘制按钮边框
                super.paint(g);
            }
        };
    }

    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        component.setOpaque(false); // 默认按钮的背景需要透明
        bg = attribute.getBackground();
    }

    /**
     * 重新计算view的大小属性
     */
    @SuppressWarnings("Duplicates")
    @Override
    protected void remeasureSize() {
        // minHeight = size + size / 5 + 10 中：size / 5用于防止像g等会出现下脚的内容被遮挡
        int size = attribute.getTextSize(), minWidth = 30, minHeight = size + size / 5 + 10;
        String text = attribute.getText();
        char[] chars = text.toCharArray();
        for (char c :
                chars) {
            // 根据英文、英文符号、中文中文符号来确定TextView至少要多大才能容的下
            if (Integer.valueOf(Integer.toString(c)) < 128) {
                minWidth += size / 2;
            } else {
                if (String.valueOf(c).matches("。？、“”——")) {
                    minWidth += 5 * size / 8;
                } else {
                    minWidth += size + 1; // 中文字符需要加1
                }
            }
        }
        setMinWidth(minWidth);
        setMinHeight(minHeight);
    }

    @Override
    public void setBackgroundColor(Color bg) {
        attribute.setBackground(bg.toString());
        this.bg = bg;
    }

    private void paintBorder(Graphics g) {
        PaintUtil.drawButtonRadius(g, bg, component.getWidth(), component.getHeight());
    }
}
