package top.totoro.swing.widget.listener;

public interface OnActivityResizeListener {

    int LEFT_TOP = 1;
    int LEFT_BOTTOM = 2;
    int RIGHT_TOP = 3;
    int RIGHT_BOTTOM = 4;
    int LEFT = 5;
    int RIGHT = 6;
    int TOP = 7;
    int BOTTOM = 8;

    /**
     * 两次测量相对的偏移
     *
     * @param x    鼠标在窗口的x轴位置
     * @param y    鼠标在窗口的y轴位置
     * @param type 窗口重置的类型
     */
    void onResizeDoing(int type, int x, int y);

}
