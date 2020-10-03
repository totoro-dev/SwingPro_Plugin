package top.totoro.swing.widget.bean;

import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.util.AttributeDefaultValue;

import java.awt.*;

public class ViewAttribute extends BaseAttribute {

    private String text = ""; // View中显示的文字
    private String textStyle = AttributeDefaultValue.SERIF; // View中显示的文字字体，默认serif
    private String textFont = AttributeDefaultValue.PLAIN; // View中显示的文字样式，默认正常
    private String textColor = AttributeDefaultValue.BLACK_COLOR; // View中显示的文字颜色，默认黑色
    private int textSize = 16; // View中显示的文字大小

    private String hintText = ""; // View中的默认文字

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextStyle() {
        // 字体转换
        switch (textStyle) {
            case AttributeDefaultValue.SERIF:
                return Font.SERIF;
            case AttributeDefaultValue.SANS_SERIF:
                return Font.SANS_SERIF;
            case AttributeDefaultValue.DIALOG:
                return Font.DIALOG;
            case AttributeDefaultValue.DIALOG_INPUT:
                return Font.DIALOG_INPUT;
            case AttributeDefaultValue.MONOSPACED:
                return Font.MONOSPACED;
        }
        return Font.SERIF;
    }

    public void setTextStyle(String textStyle) {
        this.textStyle = textStyle;
    }

    public int getTextFont() {
        // 样式转换
        switch (textFont) {
            case AttributeDefaultValue.PLAIN:
                return Font.PLAIN;
            case AttributeDefaultValue.BOLD:
                return Font.BOLD;
            case AttributeDefaultValue.ITALIC:
                return Font.ITALIC;
        }
        return Font.PLAIN;
    }

    public void setTextFont(String textFont) {
        this.textFont = textFont;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    @Override
    public String toString() {
        return "ViewAttribute{" +
                super.toString() + '\n' +
                "  text='" + text + '\'' +
                ", textStyle='" + textStyle + '\'' +
                ", textFont='" + textFont + '\'' +
                ", textColor='" + textColor + '\'' +
                ", textSize=" + textSize +
                ", hintText='" + hintText + '\'' +
                '}';
    }
}
