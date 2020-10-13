package top.totoro.swing.widget.context;

import top.totoro.swing.widget.base.Location;
import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.view.View;

import javax.swing.*;
import java.awt.*;

/**
 * 悬浮框
 */
public class PopupWindow extends Context {

    public static PopupWindow mShowingPopupWindow;

    protected JWindow mDropDownWindow;
    private final LayoutAttribute containerAttr;
    private View<?, ?> dropTarget;

    public PopupWindow() {
        mDropDownWindow = new JWindow();
        mDropDownWindow.getContentPane().setBackground(Color.white);
        containerAttr = new LayoutAttribute();
    }

    public PopupWindow(int width, int height) {
        this();
        setSize(width, height);
    }

    public PopupWindow(String layoutId, int width, int height) {
        this(width, height);
        setContentView(layoutId);
    }

    /**
     * 通过布局id加载内容到悬浮框中
     *
     * @param layoutId 布局id
     */
    public void setContentView(String layoutId) {
        layoutManager.inflate(getMainView(), layoutId);
        layoutManager.setMainLayout(getMainView());
    }

    /**
     * 刷新悬浮框的显示位置
     */
    public void refreshLocation() {
        if (dropTarget == null) return;
        if (dropTarget.getComponent() == null) return;

        Location location = null;
        if (dropTarget.getComponent().isVisible()) {
            location = Location.getLocation(dropTarget.getComponent());
        }
        if (location == null) return;
        location.yOnScreen += dropTarget.getHeight() + 1;
        mDropDownWindow.setLocation(location.xOnScreen, location.yOnScreen);

    }

    /**
     * 设置悬浮框的大小
     *
     * @param width  宽度
     * @param height 高度
     */
    public void setSize(int width, int height) {
        super.setSize(width, height);
        mDropDownWindow.setSize(width, height);
        containerAttr.setWidth(width);
        containerAttr.setHeight(height);
        getMainView().setAttribute(containerAttr);
        layoutManager.invalidate();
    }

    /**
     * 在指定的控件下方显示悬浮框
     *
     * @param dropTarget 目标控件
     */
    public void showAsDrop(View<?, ?> dropTarget) {
        prepareShow();
        this.dropTarget = dropTarget;
        refreshLocation();

        layoutManager.invalidate();

        mDropDownWindow.getContentPane().add(getMainView().getComponent());
        mDropDownWindow.setVisible(true);
        mShowingPopupWindow = this;
    }

    private void prepareShow() {
        if (mShowingPopupWindow != null) {
            mShowingPopupWindow.dismiss();
            mShowingPopupWindow = null;
        }
    }

    /**
     * 销毁悬浮框
     */
    public void dismiss() {
        if (mShowingPopupWindow != null && mShowingPopupWindow != this) {
            mShowingPopupWindow.dismiss();
            mShowingPopupWindow = null;
        }
        mDropDownWindow.dispose();
    }
}
