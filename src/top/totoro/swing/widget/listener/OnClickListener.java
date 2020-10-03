package top.totoro.swing.widget.listener;

import top.totoro.swing.widget.view.View;

public interface OnClickListener {

    /**
     * 当view被点击时触发
     *
     * @param view 被点击的View
     */
    void onClick(View view);
}
