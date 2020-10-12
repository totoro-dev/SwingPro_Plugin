package top.totoro.swing.widget.view;

import org.jdom.Attribute;
import org.jdom.Element;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.listener.OnSelectChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;

import static top.totoro.swing.widget.util.AttributeKey.*;


/**
 * 带文本的复选框
 */
public class CheckBox extends View<ViewAttribute, JPanel> {

    private JLabel boxImgLabel;
    private JLabel boxTextLabel;
    private boolean isSelected = false;

    private ImageIcon
            unselectedImg = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/check_box_unselected.png"))),
            selectedImg = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/check_box_selected.png")));

    public CheckBox(View<?, ?> parent) {
        super(parent);
        component = new JPanel(new BorderLayout(0, 0));
        boxImgLabel = new JLabel("", JLabel.LEFT);
        boxTextLabel = new JLabel("", JLabel.LEFT);
        component.add(boxImgLabel, BorderLayout.WEST);
        component.add(boxTextLabel, BorderLayout.CENTER);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        boxTextLabel.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
        boxTextLabel.setForeground(Color.decode(attribute.getTextColor()));
        Element element = attribute.getElement();
        Attribute selectedBoxIcon = element.getAttribute(selectedBoxIconKey);
        Attribute unselectedBoxIcon = element.getAttribute(unselectedBoxIconKey);
        Attribute isSelectedOn = element.getAttribute(isSelectedOnKey);
        // 初始化图标
        if (selectedBoxIcon != null) {
            URL url = getClass().getClassLoader().getResource(selectedBoxIcon.getValue());
            if (url != null) {
                selectedImg = new ImageIcon(url);
            }
        }
        if (unselectedBoxIcon != null) {
            URL url = getClass().getClassLoader().getResource(unselectedBoxIcon.getValue());
            if (url != null) {
                unselectedImg = new ImageIcon(url);
            }
        }
        // 初始化选中状态
        if (isSelectedOn != null && ("true".equals(isSelectedOn.getValue()) || "false".equals(isSelectedOn.getValue()))) {
            isSelected = Boolean.parseBoolean(isSelectedOn.getValue());
        }
        if (isSelected) {
            boxImgLabel.setIcon(selectedImg);
        } else {
            boxImgLabel.setIcon(unselectedImg);
        }
        // 要其他属性初始化结束才设置文本，确保remeasureSize的准确性
        setText(attribute.getText());
    }

    public void setText(String text) {
        if (text == null) text = "";
        boxTextLabel.setText(text);
        remeasureSize();
    }

    /**
     * 设置复选框是否选中状态
     *
     * @param isSelected 是否选中
     */
    public void setIsSelected(boolean isSelected) {
        if (this.isSelected == isSelected) return;
        this.isSelected = isSelected;
        attribute.getElement().setAttribute(isSelectedOnKey, String.valueOf(isSelected));
        if (isSelected) {
            boxImgLabel.setIcon(selectedImg);
        } else {
            boxImgLabel.setIcon(unselectedImg);
        }
        if (onSelectChangeListener != null) {
            onSelectChangeListener.onSelectChanged(attribute.getId(), isSelected);
        }
    }

    /**
     * 重新计算view的大小属性
     */
    @SuppressWarnings("Duplicates")
    protected void remeasureSize() {
        // minHeight = size + size / 5 中：size / 5用于防止像g等会出现下脚的内容被遮挡
        int size = attribute.getTextSize(), minWidth = isSelected ? selectedImg.getIconWidth() : unselectedImg.getIconWidth(), minHeight = size + size / 5;
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
        // 以图标和文本内容最大高度为标准
        if (minHeight < (isSelected ? selectedImg.getIconHeight() : unselectedImg.getIconHeight())) {
            minHeight = isSelected ? selectedImg.getIconHeight() : unselectedImg.getIconHeight();
        }
        setMinWidth(minWidth);
        setMinHeight(minHeight);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (isSelected) {
            boxImgLabel.setIcon(selectedImg);
        } else {
            boxImgLabel.setIcon(unselectedImg);
        }
        isSelected = !isSelected;
        if (onSelectChangeListener != null) {
            onSelectChangeListener.onSelectChanged(attribute.getId(), isSelected);
        }
        super.mouseClicked(e);
    }

    private OnSelectChangeListener onSelectChangeListener;

    public void addOnSelectChangeListener(OnSelectChangeListener listener) {
        onSelectChangeListener = listener;
    }

}
