package top.totoro.swing.widget.view;

import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.listener.OnTextChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 编辑框
 */
public class EditText extends View<ViewAttribute, JTextField> {

    private OnTextChangeListener onTextChangeListener;
    private String origin = "";
    public static boolean isPlugin = false; // 当前是否时插件模式

    public EditText(View parent) {
        super(parent);
        component = new JTextField();
        // 设置制表符占位数
//        component.setTabSize(4);
        component.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (component.getText().equals(attribute.getHintText())) {
                    setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (component.getText().equals("")) {
                    setHint(attribute.getHintText());
                }
            }
        });
        component.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        component.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        remeasureSize();
        component.setSize(attribute.getWidth(), attribute.getHeight());
        if (!"".equals(attribute.getHintText())) {
            setHint(attribute.getHintText());
        } else {
            setText(attribute.getText());
        }
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, attribute.getBorderColor()));
    }

    /**
     * 设置提示语
     *
     * @param hint 提示语
     */
    public void setHint(String hint) {
        // change by HLM on 2020/9/26 只有当前编辑框没有内容时才显示hint
        if (getText().equals("")) {
            component.setFont(new Font(attribute.getTextStyle(), Font.ITALIC, attribute.getTextSize()));
            component.setForeground(Color.decode("#ababab"));
            component.setText(hint);
        }
        // change end
        attribute.setHintText(hint);
    }

    /**
     * 设置编辑框文本内容
     *
     * @param text 文本内容
     */
    public void setText(String text) {
        if (text == null || "".equals(text)) {
            setHint(attribute.getHintText());
        }
        component.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
        component.setForeground(Color.decode(attribute.getTextColor()));
        component.setText(text);
    }

    /**
     * 获取编辑框的真实内容，而不返回hint内容
     *
     * @return 编辑框内容
     */
    public String getText() {
        if (component == null) return "";
        String text = component.getText();
        if (text == null || text.equals(attribute.getHintText())) return "";
        return text;
    }

    /**
     * 重新计算view的大小属性
     */
    @SuppressWarnings("Duplicates")
    protected void remeasureSize() {
        // minHeight = size + size / 5 + 10 中：size / 5用于防止像g等会出现下脚的内容被遮挡
        int size = attribute.getTextSize(), minWidth = 20, minHeight = size + size / 5 + 10;
        String text = attribute.getText();
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
        setMinWidth(minWidth);
        setMinHeight(minHeight);
    }

    /**
     * 设置编辑框文本内容变化监听，
     * 文本发生变化的1秒钟内会触发。
     *
     * @param listener 监听器
     */
    public void addOnTextChangeListener(OnTextChangeListener listener) {
        onTextChangeListener = listener;
        origin = getText();
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            if (onTextChangeListener == null || origin.equals(getText())) return;
            origin = getText();
            onTextChangeListener.onChange(origin);
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

}
