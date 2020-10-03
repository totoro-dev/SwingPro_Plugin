package top.totoro.swing.widget.listener;

/**
 * 监听布局刷新
 */
public interface InvalidateListener {
    /**
     *  布局视图可以通过
     *  {@link top.totoro.swing.widget.context.Context#requestInvalidateListener(top.totoro.swing.widget.listener.InvalidateListener)}
     *  设置布局刷新结束的监听
     *  从而实现在布局刷新完成后得到确定的控件大小等
     *  进而实现更多的功能
     */
    void onInvalidateFinished();
}
