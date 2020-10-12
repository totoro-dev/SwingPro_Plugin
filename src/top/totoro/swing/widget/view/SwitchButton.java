package top.totoro.swing.widget.view;

import org.jdom.Attribute;
import org.jdom.Element;
import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.listener.OnSwitchChangedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;

import static top.totoro.swing.widget.util.AttributeKey.*;

/**
 * 开关按钮
 */
public class SwitchButton extends View<ViewAttribute, JLabel> {
    private boolean mIsSwitchOn = false;

    private ImageIcon
            mSwitchOnIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/switch_on.png"))),
            mSwitchOffIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/switch_off.png")));

    public SwitchButton(View parent) {
        super(parent);
        component = new JLabel(mSwitchOffIcon, SwingConstants.CENTER);
        setEnterCursor(new Cursor(Cursor.HAND_CURSOR));
        component.addMouseListener(this);
    }

    /**
     * 设置开关按钮是否处于开启状态
     *
     * @param isSwitchOn 是否开启
     */
    public void setIsSwitchOn(boolean isSwitchOn) {
        if (mIsSwitchOn == isSwitchOn) return;
        mIsSwitchOn = isSwitchOn;
        attribute.getElement().setAttribute(isSwitchOnKey, String.valueOf(isSwitchOn));
        if (mIsSwitchOn) {
            component.setIcon(mSwitchOnIcon);
        } else {
            component.setIcon(mSwitchOffIcon);
        }
        if (mOnSwitchChangedListener != null) {
            mOnSwitchChangedListener.onSwitchChanged(attribute.getId(), mIsSwitchOn);
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        Element element = attribute.getElement();
        Attribute switchOn = element.getAttribute(switchOnKey);
        Attribute switchOff = element.getAttribute(switchOffKey);
        Attribute iSSwitchOn = element.getAttribute(isSwitchOnKey);
        if (switchOn != null) {
            URL url = getClass().getClassLoader().getResource(switchOn.getValue());
            if (url != null) {
                mSwitchOnIcon = new ImageIcon(url);
            }
        }
        if (switchOff != null) {
            URL url = getClass().getClassLoader().getResource(switchOff.getValue());
            if (url != null) {
                mSwitchOffIcon = new ImageIcon(url);
            }
        }
        if (iSSwitchOn != null && ("true".equals(iSSwitchOn.getValue()) || "false".equals(iSSwitchOn.getValue()))) {
            mIsSwitchOn = Boolean.parseBoolean(iSSwitchOn.getValue());
        }
        if (mIsSwitchOn) {
            component.setIcon(mSwitchOnIcon);
        } else {
            component.setIcon(mSwitchOffIcon);
        }
        reSizeAsImageSize();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mIsSwitchOn = !mIsSwitchOn;
        attribute.getElement().setAttribute(isSwitchOnKey, String.valueOf(mIsSwitchOn));
        if (mIsSwitchOn) {
            component.setIcon(mSwitchOnIcon);
        } else {
            component.setIcon(mSwitchOffIcon);
        }
        if (mOnSwitchChangedListener != null) {
            mOnSwitchChangedListener.onSwitchChanged(attribute.getId(), mIsSwitchOn);
        }
        super.mouseClicked(e);
    }

    /**
     * 根据宽高属性的wrap或确定的值，重置图片大小
     * 该方法一般在开始整体布局大小确定前调用
     */
    @SuppressWarnings("DuplicatedCode")
    private void reSizeAsImageSize() {
        int width, height;
        if (mSwitchOnIcon == null) {
            width = height = 0;
        } else {
            width = mSwitchOnIcon.getIconWidth();
            height = mSwitchOnIcon.getIconHeight();
        }
        if (attribute.getWidth() == BaseAttribute.WRAP_CONTENT) {
            setMinWidth(width);
        }
        if (attribute.getHeight() == BaseAttribute.WRAP_CONTENT) {
            setMinHeight(height);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        reSizeAsImageSize();
    }

    private OnSwitchChangedListener mOnSwitchChangedListener;

    public void addOnSwitchChangedListener(OnSwitchChangedListener listener) {
        mOnSwitchChangedListener = listener;
    }
}
