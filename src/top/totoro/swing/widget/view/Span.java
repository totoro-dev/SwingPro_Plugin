package top.totoro.swing.widget.view;

import top.totoro.swing.widget.bean.ViewAttribute;

import javax.swing.*;

/**
 * 一个简单的占位视图
 */
public class Span extends View<ViewAttribute, JPanel> {
    public Span(View parent) {
        super(parent);
        component = new JPanel();
    }
}
