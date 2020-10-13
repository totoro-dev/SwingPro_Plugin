package top.totoro.swing.widget.context;

import com.sun.istack.internal.NotNull;
import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.base.Location;
import top.totoro.swing.widget.base.Size;
import top.totoro.swing.widget.event.MotionEvent;
import top.totoro.swing.widget.interfaces.ContextWrapper;
import top.totoro.swing.widget.layout.LinearLayout;
import top.totoro.swing.widget.listener.InvalidateListener;
import top.totoro.swing.widget.manager.ActivityManager;
import top.totoro.swing.widget.manager.LayoutManager;
import top.totoro.swing.widget.util.AnimateUtil;
import top.totoro.swing.widget.view.View;

import java.util.ArrayList;
import java.util.List;

public class Context implements ContextWrapper {

    private Location location;
    private Size size;
    private BaseLayout mainView = new LinearLayout(null);
    protected LayoutManager layoutManager = new LayoutManager();
    protected List<InvalidateListener> invalidateListenerList = new ArrayList<>();

    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void requestInvalidateListener(InvalidateListener listener) {
        invalidateListenerList.remove(listener);
        invalidateListenerList.add(listener);
    }

    public Context() {
        mainView.setContext(this);
    }

    /**
     * 通过指定上下文来启动一个activity界面
     *
     * @param context 指定上下文
     * @param target  activity界面
     */
    public void startActivity(@NotNull Context context, Class<? extends Activity> target) {
        Activity activity = ActivityManager.getOrCreateActivity(target);
        ActivityManager.setTopActivity(activity);
        if (context instanceof Activity) {
            activity.setParentActivity((Activity) context);
            activity.resetLocation(((Activity) context).getFrame().getLocation().x, ((Activity) context).getFrame().getLocation().y);
            AnimateUtil.transparentOut((Activity) context, 0.5f, () -> {
                context.onStop();
                if (activity.isOnRestart()) {
                    activity.onStart();
                } else {
                    activity.setLocation(context.location);
                    activity.setSize(context.size);
                    activity.onCreate();
                }
                AnimateUtil.transparentIn(activity, 0.75f, () -> activity.setCanBack(true));
            });
        } else {
            activity.setParentActivity(null);
            activity.setCanBack(false);
            activity.setLocation(null);
            AnimateUtil.zoomIn(activity, new Size(500, 500), 0.75f);
        }
    }

    /**
     * 以当前作为上下文，启动一个activity界面
     *
     * @param target activity界面
     */
    public void startActivity(Class<? extends Activity> target) {
        Activity activity = ActivityManager.getOrCreateActivity(target);
        ActivityManager.setTopActivity(activity);
        if (activity.isOnRestart()) {
            activity.onStart();
        } else {
            activity.setLocation(location);
            activity.setSize(size);
            activity.onCreate();
        }
        AnimateUtil.transparentIn(activity, 0.75f);
    }

    /**
     * 在当前上下文中获取指定id的视图元素
     *
     * @param id 视图id
     * @return 视图元素（对象）
     */
    public View findViewById(String id) {
        return mainView.findViewById(id);
    }

    /**
     * 获取上下文的根视图
     *
     * @return 根视图
     */
    public BaseLayout getMainView() {
        return mainView;
    }

    /**
     * 刷新整个上下文界面
     */
    public void invalidate() {
        if (mainView != null && layoutManager != null) {
            mainView.invalidate();
            layoutManager.invalidate();
            for (InvalidateListener listener :
                    invalidateListenerList) {
                listener.onInvalidateFinished();
            }
        }
    }

    public void dispatchMotionEvent(MotionEvent event) {
        // just for handle view event
    }

    public void onCreate() {
    }

    public void onStart() {

    }

    public void onRestart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLocation(int x, int y) {
        this.location = new Location(x, y);
    }

    public Location getLocation() {
        return location;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public void setSize(int width, int height) {
        this.size = new Size(width, height);
    }
}
