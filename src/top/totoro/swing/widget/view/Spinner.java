package top.totoro.swing.widget.view;

import org.jdom.Attribute;
import org.jdom.Element;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.listener.OnClickListener;
import top.totoro.swing.widget.listener.OnItemSelectedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

import static top.totoro.swing.widget.util.AttributeKey.*;

/**
 * 下拉框
 */
public class Spinner extends View<ViewAttribute, JPanel> implements OnClickListener {
    private JLabel mSelectedLabel;
    private JLabel mDropDownButton;
    protected JWindow mDropDownWindow;
    private int mSelectedPosition = -1;
    private String[] mStringArray;
    private JLabel[] mDropDownItems;
    private Color mSelectedColor = Color.white;
    private Color mEnterColor = Color.white;

    public Spinner(View parent) {
        super(parent);
        mDropDownWindow = new JWindow();
        component = new JPanel(new BorderLayout());
        mSelectedLabel = new JLabel("", JLabel.CENTER);
        mDropDownButton = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/drop_down_arrow.png"))), JLabel.CENTER);
        component.add(mSelectedLabel, BorderLayout.CENTER);
        component.add(mDropDownButton, BorderLayout.EAST);
        mDropDownWindow.setAlwaysOnTop(true);
        mDropDownWindow.getContentPane().setBackground(Color.white);
        addOnClickListener(this);
    }

    /**
     * 设置匹配字符串内容的下拉框选项为选中状态
     *
     * @param item 匹配字符串
     */
    public void setSelectedItem(String item) {
        if (mStringArray == null || mStringArray.length == 0) return;
        for (int i = 0; i < mStringArray.length; i++) {
            if (mStringArray[i].equals(item)) {
                mSelectedPosition = i;
                mSelectedLabel.setText(item);
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onSelected(attribute.getId(), i, item);
                }
                break;
            }
        }
    }

    /**
     * 获取选中的项的文本
     *
     * @return 选中文本
     */
    public String getText() {
        if (mSelectedLabel == null) return "";
        return mSelectedLabel.getText();
    }

    /**
     * 设置指定下拉框条目位置的选项为选中状态
     *
     * @param position 条目位置
     */
    public void setSelectedItem(int position) {
        if (mStringArray == null || mStringArray.length == 0 || position < 0 || position >= mStringArray.length) return;
        mSelectedPosition = position;
        mSelectedLabel.setText(mStringArray[position]);
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onSelected(attribute.getId(), position, mStringArray[position]);
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        mSelectedLabel.setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
        mSelectedLabel.setText(attribute.getText());
        mSelectedLabel.setForeground(Color.decode(attribute.getTextColor()));
        component.setSize(attribute.getWidth(), attribute.getHeight());

        /* add by HLM on 2020/7/27 为下拉框的window设置边框 */
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                attribute.getBorderColor()));
        mDropDownWindow.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1,
                attribute.getBorderColor()));
        /* add end */

        Element element = attribute.getElement();
        Attribute arrayAttr = element.getAttribute(arrayAttrKey);
        if (arrayAttr != null) {
            mStringArray = arrayAttr.getValue().split(",");
        }
        Attribute enterColorAttr = element.getAttribute(enterColorKey);
        if (enterColorAttr != null) {
            String colorVal = enterColorAttr.getValue();
            if (colorVal.startsWith("#") && (colorVal.length() == 4 || colorVal.length() == 7 || colorVal.length() == 9)) {
                mEnterColor = Color.decode(colorVal);
            }
        }
        Attribute selectedColorAttr = element.getAttribute(selectedColorKey);
        if (selectedColorAttr != null) {
            String colorVal = selectedColorAttr.getValue();
            if (colorVal.startsWith("#") && (colorVal.length() == 4 || colorVal.length() == 7)) {
                mSelectedColor = Color.decode(colorVal);
            }
        }
        setSelectedItem(0);
    }

    @Override
    public void onClick(View view) {
        if (View.mShowingSpinner != null) {
            View.mShowingSpinner.dismiss();
        }
        View.mShowingSpinner = this;
        /* 确定下拉框的下拉位置 */
        refreshLocation();
        setDropDownItems();

        mDropDownWindow.setSize(component.getWidth(), component.getHeight() * mStringArray.length);

        mDropDownWindow.setVisible(true);
    }

    /* 创建下拉框中的内容 */
    private void setDropDownItems() {
        mDropDownWindow.getContentPane().removeAll();
        if (mStringArray == null) {
            if (attribute.getText() == null || "".equals(attribute.getText()))
                mStringArray = new String[]{"无选项"};
            else mStringArray = new String[]{attribute.getText()};
        }
        mDropDownWindow.getContentPane().setLayout(new GridLayout(mStringArray.length, 1));
        mDropDownItems = new JLabel[mStringArray.length];
        for (int i = 0; i < mDropDownItems.length; i++) {
            mDropDownItems[i] = new JLabel(mStringArray[i], JLabel.CENTER);
            mDropDownItems[i].setSize(component.getWidth(), component.getHeight());
            mDropDownItems[i].setFont(new Font(attribute.getTextStyle(), attribute.getTextFont(), attribute.getTextSize()));
            mDropDownWindow.add(mDropDownItems[i]);
            mDropDownItems[i].setOpaque(true);
            mDropDownItems[i].setBackground(Color.white);
            if (i < mDropDownItems.length - 1) {
                mDropDownItems[i].setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#dbdbdb")));
            }
            if (mStringArray[i].equals(mSelectedLabel.getText())) {
                mDropDownItems[i].setBackground(mSelectedColor);
                mSelectedPosition = i;
            }
            int finalI = i;
            mDropDownItems[i].addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mSelectedLabel.setText(mStringArray[finalI]);
                    mDropDownWindow.dispose();
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onSelected(attribute.getId(), finalI, mStringArray[finalI]);
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (finalI != mSelectedPosition) {
                        mDropDownItems[finalI].setBackground(mEnterColor);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (finalI != mSelectedPosition) {
                        mDropDownItems[finalI].setBackground(Color.white);
                    }
                }
            });
        }
    }

    private OnItemSelectedListener onItemSelectedListener;

    public void addOnItemSelectedListener(OnItemSelectedListener listener) {
        onItemSelectedListener = listener;
    }

    /**
     * 刷新下拉框的显示位置
     */
    public void refreshLocation() {
        Point spinnerLocation = component.getLocationOnScreen();
        mDropDownWindow.setLocation(spinnerLocation.x,
                spinnerLocation.y + component.getHeight());
    }

    /**
     * 取消下拉框的显示
     */
    public void dismiss() {
        View.mShowingSpinner.mDropDownWindow.dispose();
        View.mShowingSpinner = null;
    }
}
