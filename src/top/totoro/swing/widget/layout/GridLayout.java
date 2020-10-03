package top.totoro.swing.widget.layout;

import org.jdom.Attribute;
import org.jdom.Element;
import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.util.AttributeKey;
import top.totoro.swing.widget.view.View;

public class GridLayout extends BaseLayout {

    private int column = 1; // 默认只有一列
    private int gapVertical = 0;
    private int gapHorizontal = 0;

    public GridLayout(View parent) {
        super(parent);
    }

    @Override
    public void setAttribute(LayoutAttribute attribute) {
        super.setAttribute(attribute);
        Element element = attribute.getElement();
        Attribute columnAttr = element.getAttribute(AttributeKey.column);
        if (columnAttr != null) {
            column = Integer.parseInt(columnAttr.getValue());
        }
        setColumn(column);

        Attribute gap = element.getAttribute(AttributeKey.GAP);
        if (gap != null && gap.getValue().length() > 0) {
            this.gapVertical = this.gapHorizontal = Integer.parseInt(gap.getValue());
        } else {
            Attribute gapVerticalAttr = element.getAttribute(AttributeKey.GAP_VERTICAL);
            Attribute gapHorizontalAttr = element.getAttribute(AttributeKey.GAP_HORIZONTAL);
            if (gapVerticalAttr != null && gapVerticalAttr.getValue().length() > 0) {
                this.gapVertical = Integer.parseInt(gapVerticalAttr.getValue());
            }
            if (gapHorizontalAttr != null && gapHorizontalAttr.getValue().length() > 0) {
                this.gapHorizontal = Integer.parseInt(gapHorizontalAttr.getValue());
            }
        }
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
        resetGrid();
    }

    public void setGap(int gap) {
        if (gap != gapVertical || gap != gapHorizontal) {
            gapVertical = gapHorizontal = gap;
            invalidateSuper();
        }
    }

    public int getGapVertical() {
        return gapVertical;
    }

    public void setGapVertical(int gapVertical) {
        if (gapVertical != this.gapVertical) {
            this.gapVertical = gapVertical;
            invalidateSuper();
        }
    }

    public int getGapHorizontal() {
        return gapHorizontal;
    }

    public void setGapHorizontal(int gapHorizontal) {
        if (gapHorizontal != this.gapHorizontal) {
            this.gapHorizontal = gapHorizontal;
            invalidateSuper();
        }
    }

    /**
     * 刷新网格的排布，比如里面某一项设置不可见或者可见时，触发网格变化
     */
    public void resetGrid() {
        if (component == null) return;
        int visibleSize = getVisibleSize();
        int row = visibleSize / column;
        if (column * row < visibleSize) row++;
        component.setLayout(new java.awt.GridLayout(row, column, gapHorizontal, gapVertical));
    }

    /**
     * 获取当前GridLayout布局中真实可见的子控件数量。
     * 可以用于测量网格的显示数量。
     *
     * @return 可见控件数量
     */
    public int getVisibleSize() {
        int visibleSize = 0;
        component.removeAll();
        for (View<?, ?> sonView : getSonViews()) {
            if (sonView.getVisible()) {
                component.add(sonView.getComponent());
                visibleSize++;
            }
        }
        return visibleSize;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        resetGrid();
    }
}
