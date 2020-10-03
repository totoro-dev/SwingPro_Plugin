package top.totoro.swing.widget.view;

import org.jdom.Attribute;
import org.jdom.Element;
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
        if (mIsSwitchOn) {
            component.setIcon(mSwitchOnIcon);
        } else {
            component.setIcon(mSwitchOffIcon);
        }
    }

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
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (mIsSwitchOn) {
            component.setIcon(mSwitchOffIcon);
        } else {
            component.setIcon(mSwitchOnIcon);
        }
        mIsSwitchOn = !mIsSwitchOn;
        if (mOnSwitchChangedListener != null) {
            mOnSwitchChangedListener.onSwitchChanged(attribute.getId(), mIsSwitchOn);
        }
        super.mouseClicked(e);
    }

    private OnSwitchChangedListener mOnSwitchChangedListener;

    public void addOnSwitchChangedListener(OnSwitchChangedListener listener) {
        mOnSwitchChangedListener = listener;
    }
}
