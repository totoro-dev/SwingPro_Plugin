package top.totoro.swing.widget.layout;

import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.view.View;

public class LinearLayout extends BaseLayout {

    public LinearLayout(View parent) {
        super(parent);
    }

    @Override
    public void setAttribute(LayoutAttribute attribute) {
        super.setAttribute(attribute);
        component.setSize(attribute.getWidth(), attribute.getHeight());
    }

}
