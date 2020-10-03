package top.totoro.swing.widget.base;

import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.context.Context;
import top.totoro.swing.widget.view.View;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class BaseLayout extends View<LayoutAttribute, JPanel> {

    public int currNoMatchWidth = 0; // 当前layout中包含的组件总宽度
    public int currNoMatchHeight = 0; // 当前layout中包含的组件总高度
    public List<View> matchParentWidthViews = new LinkedList<>();
    public List<View> matchParentHeightViews = new LinkedList<>();

    public BaseLayout(View parent) {
        super(parent);
        component = new JPanel();
        component.setLayout(null);
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        // 向子View继续设置Context，使之能够拥有上下文，进行更多的操作，因为可能有的view是动态添加的
        List<View> sons = getSonViews();
        for (View son : sons) {
            son.setContext(context);
        }
    }

    /**
     * 向布局中添加子控件或子布局
     *
     * @param childView 子控件或子布局
     */
    public void addChildView(View<?, ?> childView) {
        if (childView == null) return;
        childView.setParent(this);
        component.add(childView.getComponent());
        if (getSonViews().size() > 0) {
            View<?, ?> pre = getSonByIndex(getSonViews().size() - 1);
            childView.setPreView(pre);
        }
        addSon(childView);
    }

    /**
     * 重新测量所有含match_parent属性的子控件的宽度
     */
    public void remeasureMatchParentChildViewWidth() {
        if (getAttribute().getVisible() == BaseAttribute.GONE) {
            getComponent().setSize(0, 0);
            return;
        }
        if (matchParentWidthViews.size() == 0) return;
        if (getAttribute().getWidth() == BaseAttribute.WRAP_CONTENT) return;
        int width = (component.getWidth() - currNoMatchWidth) / matchParentWidthViews.size();
        for (View v :
                matchParentWidthViews) {
            v.getComponent().setSize(width, v.getComponent().getHeight());
        }
    }

    /**
     * 重新测量所有含match_parent属性的子控件的高度
     */
    public void remeasureMatchParentChildViewHeight() {
        if (getAttribute().getVisible() == BaseAttribute.GONE) {
            getComponent().setSize(0, 0);
            return;
        }
        if (matchParentHeightViews.size() == 0) return;
        if (getAttribute().getHeight() == BaseAttribute.WRAP_CONTENT) return;
        int height = (component.getHeight() - currNoMatchHeight) / matchParentHeightViews.size();
        for (View v : matchParentHeightViews) {
            v.getComponent().setSize(v.getComponent().getWidth(), height);
        }
    }

    /**
     * 刷新该节点布局，及其子布局
     */
    public void invalidate() {
        super.invalidate();
        currNoMatchWidth = 0;
        currNoMatchHeight = 0;
        matchParentWidthViews.clear();
        matchParentHeightViews.clear();
        LinkedList<View> sonViews = getSonViews();
        for (View son : sonViews) {
            if (son == null) continue;
            son.invalidate();
        }
    }

}
