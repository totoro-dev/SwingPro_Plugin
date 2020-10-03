package top.totoro.swing.widget.base;

import java.awt.*;

public class Location {
    public int xOnParent, yOnParent;
    public int xOnScreen, yOnScreen;

    /**
     * 获取可见组件的位置，并生成其Location
     *
     * @param target 组件
     * @return 如果组件不可见为null，否则返回包含组件在屏幕上位置和相对父组件位置的Location
     */
    public static top.totoro.swing.widget.base.Location getLocation(Component target) {
        if (target == null || !target.isVisible()) return null;
        Point pointOnParent = target.getLocation();
        top.totoro.swing.widget.base.Location location = new top.totoro.swing.widget.base.Location(pointOnParent.x, pointOnParent.y);
        Point locationOnScreen = target.getLocationOnScreen();
        location.xOnScreen = locationOnScreen.x;
        location.yOnScreen = locationOnScreen.y;
        return location;
    }

    public Location(int xOnParent, int yOnParent) {
        this.xOnParent = xOnParent;
        this.yOnParent = yOnParent;
    }
}
