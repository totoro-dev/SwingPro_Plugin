package top.totoro.swing.widget.event;

import java.awt.event.MouseEvent;

public class MotionEvent extends InputEvent {

    private static final String TAG = "MotionEvent";

    public static final int ACTION_DOWN             = 0;

    public static final int ACTION_UP               = 1;

    public static final int ACTION_CLICKED          = 2;

    public static final int ACTION_INSIDE           = 3;

    public static final int ACTION_OUTSIDE          = 4;

    public static final int ACTION_MOVE             = 5;

    public static final int ACTION_DRAG             = 6;

    public static final int ACTION_SCROLL           = 8;

    private int x, y;
    private int action;

    public MotionEvent() {

    }

    public MotionEvent(MouseEvent mouseEvent, int action) {
        if (mouseEvent.getComponent().isShowing()) {
            x = mouseEvent.getComponent().getLocationOnScreen().x + mouseEvent.getX();
            y = mouseEvent.getComponent().getLocationOnScreen().y + mouseEvent.getY();
        }
        this.action = action;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "MotionEvent{" +
                "x=" + x +
                ", y=" + y +
                ", action=" + action +
                '}';
    }
}
