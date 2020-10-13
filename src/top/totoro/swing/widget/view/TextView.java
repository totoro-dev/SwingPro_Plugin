package top.totoro.swing.widget.view;

import org.jdom.Attribute;
import org.jdom.Element;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.util.AttributeDefaultValue;
import top.totoro.swing.widget.util.AttributeKey;

import javax.swing.*;
import java.awt.*;

/**
 * 文本框
 */
public class TextView extends View<ViewAttribute, JLabel> {

    private String alignment = AttributeDefaultValue.center;

    public TextView(View parent) {
        super(parent);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setAttribute(ViewAttribute attribute) {
        // add by HLM(dragon) on 2020/9/26
        // 由于Button继承子TextView，在这里初始化避免对component多次new
        if (component == null) {
            component = new JLabel("");
        }
        // 添加对文本对齐方式的设置
        Element element = attribute.getElement();
        Attribute alignmentAttr = element.getAttribute(AttributeKey.TEXT_ALIGNMENT);
        if (alignmentAttr != null) {
            alignment = alignmentAttr.getValue();
        }
        setAlignment();
        // add end
        super.setAttribute(attribute);
        component.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
        component.setText(attribute.getText());
        component.setForeground(Color.decode(attribute.getTextColor()));
        remeasureSize();
        component.setSize(attribute.getWidth(), attribute.getHeight());
    }

    /**
     * 设置文本对齐方式
     *
     * @param alignment 对齐方式
     * @see AttributeDefaultValue#left 左（上下居中）
     * @see AttributeDefaultValue#right 右（上下居中）
     * @see AttributeDefaultValue#top 上（左右居中）
     * @see AttributeDefaultValue#bottom 下（左右居中）
     * @see AttributeDefaultValue#center 上下左右居中
     * @see AttributeDefaultValue#leftAndTop 左上
     * @see AttributeDefaultValue#leftAndBottom 坐下
     * @see AttributeDefaultValue#rightAndTop 右上
     * @see AttributeDefaultValue#rightAndBottom 右下
     */
    public void setAlignment(String alignment) {
        this.alignment = alignment;
        setAlignment();
    }

    private void setAlignment() {
        int verticalAli = JLabel.CENTER;
        int horizontalAli = JLabel.CENTER;
        String[] alignments = alignment.split("\\|");
        for (String alignment : alignments) {
            switch (alignment) {
                case AttributeDefaultValue.left:
                    horizontalAli = JLabel.LEFT;
                    break;
                case AttributeDefaultValue.right:
                    horizontalAli = JLabel.RIGHT;
                    break;
            }
            switch (alignment) {
                case AttributeDefaultValue.top:
                    verticalAli = JLabel.TOP;
                    break;
                case AttributeDefaultValue.bottom:
                    verticalAli = JLabel.BOTTOM;
                    break;
            }
        }
        component.setVerticalAlignment(verticalAli);
        component.setHorizontalAlignment(horizontalAli);
    }

    /**
     * 重新计算view的大小属性
     */
    @SuppressWarnings("Duplicates")
    protected void remeasureSize() {
        // minHeight = size + size / 5 中：size / 5用于防止像g等会出现下脚的内容被遮挡
        int size = attribute.getTextSize(), minWidth = 0, minHeight = size + size / 5;
        String text = attribute.getText();
        if (text != null) {
            char[] chars = text.toCharArray();
            for (char c :
                    chars) {
                // 根据英文、英文符号、中文中文符号来确定TextView至少要多大才能容的下
                if (Integer.parseInt(Integer.toString(c)) < 128) {
                    minWidth += size / 2;
                } else {
                    if (String.valueOf(c).matches("。？、“”——")) {
                        minWidth += 5 * size / 8;
                    } else {
                        minWidth += size + 1; // 中文字符需要加1
                    }
                }
            }
        } else {
            minHeight = 0;
        }
        setMinWidth(minWidth);
        setMinHeight(minHeight);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        remeasureSize();
    }

    /**
     * 设置文本内容
     *
     * @param text 文本
     */
    public void setText(String text) {
        attribute.setText(text);
        component.setText(text);
        invalidateSuper();
    }

    /**
     * 获取当前文本框中显示的文本内容
     *
     * @return 显示的文本内容
     */
    public String getText() {
        if (component == null) return "";
        return component.getText();
    }

    /**
     * 设置字体大小
     *
     * @param size 字体大小
     */
    public void setTextSize(int size) {
        if (attribute.getTextSize() == size) return;
        attribute.setTextSize(size);
        component.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
        invalidateSuper();
    }

    /**
     * 设置字体颜色
     *
     * @param color 字体颜色
     */
    public void setTextColor(Color color) {
        attribute.setTextColor(color.toString());
        component.setForeground(color);
    }

    /**
     * 设置字体
     *
     * @param style SERIF字体{@link AttributeDefaultValue#SERIF}
     *              SANS_SERIF字体{@link AttributeDefaultValue#SANS_SERIF}
     *              DIALOG字体{@link AttributeDefaultValue#DIALOG}
     *              DIALOG_INPUT字体{@link AttributeDefaultValue#DIALOG_INPUT}
     *              MONOSPACED字体{@link AttributeDefaultValue#MONOSPACED}
     */
    public void setTextStyle(String style) {
        attribute.setTextStyle(style);
        component.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
    }

    /**
     * 设置字体的样式：正常、加粗、斜体
     *
     * @param font 正常样式{@link AttributeDefaultValue#PLAIN}
     *             加粗样式{@link AttributeDefaultValue#BOLD}
     *             斜体样式{@link AttributeDefaultValue#ITALIC}
     */
    public void setTextFont(String font) {
        attribute.setTextFont(font);
        component.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
    }

}
