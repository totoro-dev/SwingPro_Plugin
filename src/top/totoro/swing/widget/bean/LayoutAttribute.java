package top.totoro.swing.widget.bean;

import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.util.AttributeDefaultValue;

public class LayoutAttribute extends BaseAttribute {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    private int orientation = VERTICAL; // 组件方向
    private String gravity = AttributeDefaultValue.left; // 相对位置

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getGravity() {
        return gravity;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }

    @Override
    public String toString() {
        return "LayoutAttribute{" +
                "orientation=" + orientation +
                ", gravity='" + gravity + '\'' +
                ", " + super.toString() +
                '}';
    }
}
