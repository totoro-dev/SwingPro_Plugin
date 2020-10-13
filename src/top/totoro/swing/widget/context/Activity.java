package top.totoro.swing.widget.context;

import com.intellij.ui.JBColor;
import top.totoro.swing.widget.bar.ActionBar;
import top.totoro.swing.widget.base.DefaultAttribute;
import top.totoro.swing.widget.base.Location;
import top.totoro.swing.widget.base.Size;
import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.event.MotionEvent;
import top.totoro.swing.widget.listener.OnActionBarClickListener;
import top.totoro.swing.widget.listener.OnActionBarResizeListener;
import top.totoro.swing.widget.listener.OnActivityDragListener;
import top.totoro.swing.widget.listener.OnActivityResizeListener;
import top.totoro.swing.widget.listener.deafultImpl.DefaultActivityResizeMouseListener;
import top.totoro.swing.widget.manager.ActivityManager;
import top.totoro.swing.widget.manager.DialogManager;
import top.totoro.swing.widget.manager.ServiceManager;
import top.totoro.swing.widget.util.AnimateUtil;
import top.totoro.swing.widget.util.Log;
import top.totoro.swing.widget.util.SwingConstants;
import top.totoro.swing.widget.view.View;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static top.totoro.swing.widget.event.MotionEvent.*;

@SuppressWarnings("unused")
public class Activity extends Context implements OnActionBarClickListener, OnActivityDragListener, OnActionBarResizeListener {

    private final ScheduledExecutorService resizeExecutor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> resizeFuture;

    private JFrame frame;
    private OnActivityResizeListener resizeListener;
    private DefaultActivityResizeMouseListener defaultActivityResizeMouseListener;
    private boolean resizeable = true; // 是否允许窗口缩放
    public ActionBar mainBar;
    private final JPanel actionBarPanel = new JPanel(null);
    private Location normalLocation;
    private Size normalSize;
    private boolean isOnRestart;
    private Activity parentActivity;
    private boolean isShowing = false;

    public Activity() {
        super();
        /* add by HLM on 2020/7/27 预先加载全局默认属性值 */
        DefaultAttribute.loadDefaultAttribute(getClass());
        /* add end */
        defaultActivityResizeMouseListener = new DefaultActivityResizeMouseListener();
        addOnActivityResizeListener(defaultActivityResizeMouseListener.DEFAULT_RESIZE_LISTENER);
        defaultActivityResizeMouseListener.setOnActivityResizeListener(resizeListener);
    }

    public static Activity newInstance(Size size) {
        Activity activity = new Activity();
        try {
            if (size != null) {
                activity.setSize(size);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return activity;
    }

    public static Activity newInstance(Size size, Location location) {
        Activity activity = new Activity();
        try {
            if (size != null) {
                activity.setSize(size);
            }
            if (location != null) {
                activity.setLocation(location);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return activity;
    }

    @Override
    public void dispatchMotionEvent(MotionEvent event) {
        super.dispatchMotionEvent(event);
        int x, y;
        switch (event.getAction()) {
            case ACTION_DOWN:
                defaultActivityResizeMouseListener.mousePressed();
                break;
            case ACTION_UP:
                defaultActivityResizeMouseListener.mouseReleased();
                break;
            case ACTION_CLICKED:
                break;
            case ACTION_INSIDE:
                break;
            case ACTION_OUTSIDE:
                break;
            case ACTION_MOVE:
                x = event.getX() - frame.getLocationOnScreen().x;
                y = event.getY() - frame.getLocationOnScreen().y;
                defaultActivityResizeMouseListener.mouseMoved(x, y);
                break;
            case ACTION_DRAG:
                x = event.getX() - frame.getLocationOnScreen().x;
                y = event.getY() - frame.getLocationOnScreen().y;
                defaultActivityResizeMouseListener.mouseDragged(x, y);
                break;
        }
    }

    public void setOnRestart(boolean onRestart) {
        isOnRestart = onRestart;
    }

    public boolean isOnRestart() {
        return isOnRestart;
    }

    public void setParentActivity(Activity parentActivity) {
        Log.d(this, "setParentActivity = " + parentActivity);
        this.parentActivity = parentActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        frame = new JFrame() {

            @Override
            public void setVisible(boolean b) {
                super.setVisible(b);
                isShowing = b;
            }

            @Override
            public void dispose() {
                super.dispose();
                onDestroy();
            }
        };

        frame.addWindowStateListener(state -> {
            if (state.getNewState() == 1 || state.getNewState() == 7) {
                // 窗口最小化，对话框需要同时隐藏（除了Toast）
                if (DialogManager.getTopDialog() != null
                        && !(DialogManager.getTopDialog() instanceof Toast)
                        && DialogManager.getTopDialog().isShowing()) {
                    DialogManager.getTopDialog().hide(true);
                }
                if (PopupWindow.mShowingPopupWindow != null) {
                    PopupWindow.mShowingPopupWindow.dismiss();
                }
                isShowing = false;
            } else if (state.getNewState() == 0) {
                // 窗口恢复，同时恢复对话框的显示状态（除了Toast）
                if (DialogManager.getTopDialog() != null
                        && !(DialogManager.getTopDialog() instanceof Toast)
                        && DialogManager.getTopDialog().isShowing()) {
                    DialogManager.getTopDialog().show();
                }
                isShowing = true;
            }
        });

        frame.getContentPane().setLayout(null);
        frame.getContentPane().removeAll();

        frame.setUndecorated(true); // 去除窗体的标题栏

        // 设置窗体大小
        if (getSize() != null) {
            frame.setSize(getSize().width, getSize().height);
        } else {
            // 默认全屏
            frame.setSize(SwingConstants.getScreenSize());
        }

        // 设置窗体位置
        if (getLocation() != null) {
            frame.setLocation(getLocation().xOnParent, getLocation().yOnParent);
        } else {
            // 默认居中
            frame.setLocationRelativeTo(null);
            setLocation(Location.getLocation(frame));
        }

        defaultActivityResizeMouseListener.init(this);

        normalLocation = getLocation();
        normalSize = getSize();

        // 设置ActionBar
        actionBarPanel.setSize(frame.getWidth(), 0);
        frame.add(actionBarPanel);
        mainBar = new ActionBar(actionBarPanel);
        resetActionBar();
        // 默认以窗口的类名作为标题
        setTitle(getClass().getSimpleName());
        // 设置应用图标
        setIcon(DefaultAttribute.appIcon);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isOnRestart) {
            onRestart();
        }
        onResume();
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    /**
     * 窗体被显示时调用
     */
    @Override
    public void onResume() {
        super.onResume();
        // 设置窗体可见
        if (frame != null && !frame.isVisible()) {
            // 解决初始化窗口时的闪屏问题
            // 由于初始化窗口时是透明度渐变过程，如果一开始就是非透明的（opacity = 1），会出现闪屏
            frame.setOpacity(0);
            frame.setVisible(true);
            setLocation(Location.getLocation(frame));
            Log.d(this, "x = " + getLocation().xOnParent + ", y = " + getLocation().yOnParent);
        }

        mainBar.addOnActionBarClickListener(this);
        mainBar.addOnActivityDragListener(this);
        mainBar.addOnActionBarResizeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        for (Service boundService : ServiceManager.getBoundServices(this)) {
            boundService.onPause();
        }
    }

    /**
     * 该方法会在窗体最小化时调用
     */
    @Override
    public void onStop() {
        super.onStop();
        // 设置窗体不可见
        if (frame != null && frame.isVisible()) {
            frame.setVisible(false);
        }
        for (Service boundService : ServiceManager.getBoundServices(this)) {
            boundService.onStop();
        }
    }

    /**
     * 窗体被关闭时调用
     */
    @Override
    public void onDestroy() {
        onPause();
        onStop();
        for (Service boundService : ServiceManager.getBoundServices(this)) {
            boundService.stopService();
        }
        super.onDestroy();
    }

    public boolean isVisible() {
        return frame != null && frame.isVisible() && isShowing;
    }

    /**
     * 退出当前界面并销毁
     */
    public void finish() {
        AnimateUtil.transparentOut(this, 0.5f, () -> {
            frame.setVisible(false);
            frame.dispose();
            Log.d(this, "finish() dispose " + (ActivityManager.getTopActivity() != null));
            if (parentActivity != null && ActivityManager.getTopActivity() != null) {
                parentActivity.onStart();
                AnimateUtil.transparentIn(parentActivity, 0.75f, () -> {
                    ActivityManager.removeActivity(this);
                    ActivityManager.setTopActivity(parentActivity);
                });
            } else {
                ActivityManager.setTopActivity(null);
                // 当打开多个activity后关闭所有窗口无法完全退出应用
                ActivityManager.finishAll(this);
                // 避免存在隐藏但没有销毁的dialog导致无法退出应用
                if (DialogManager.getTopDialog() != null && DialogManager.getTopDialog().isShowing()) {
                    DialogManager.getTopDialog().dismiss();
                }
            }
        });
    }

    /**
     * 设置顶部标题栏的高度
     *
     * @param height 指定高度
     * @see ActionBar.Height#MIN
     * @see ActionBar.Height#MID
     * @see ActionBar.Height#MAX
     */
    protected void setActionBarHeight(ActionBar.Height height) {
        mainBar.setHeight(height);
    }

    /**
     * 设置顶部标题栏边框的颜色
     *
     * @param color 边框颜色
     */
    protected void setActionBarBorderColor(Color color) {
        mainBar.setBorder(1, color);
    }

    /**
     * 设置界面显示的图标
     *
     * @param iconPath 图标的资源路径
     */
    public void setIcon(String iconPath) {
        if (frame != null) {
            URL url = getClass().getClassLoader().getResource(iconPath);
            if (url != null) {
                frame.setIconImage(Toolkit.getDefaultToolkit().getImage(url));
            } else {
                Log.e(this, "Can't load icon with path ：" + iconPath);
            }
        }
    }

    /**
     * 设置界面显示的标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        if (mainBar != null) {
            mainBar.setTitleText(title);
        }
        if (frame != null) {
            frame.setTitle(title);
        }
    }

    /**
     * 设置界面显示的标题颜色
     *
     * @param color 颜色
     */
    public void setTitleColor(Color color) {
        mainBar.setTitleColor(color);
    }

    /**
     * 设置当前页面顶部标题栏是否显示返回按键
     *
     * @param canBack 是否显示
     */
    public void setCanBack(boolean canBack) {
        if (mainBar != null && parentActivity != null) {
            Log.d(this, "canBack : " + canBack);
            mainBar.canBack(true);
        }
    }

    /**
     * 窗口变化时ActionBar会被重置
     */
    private void resetActionBar() {
        actionBarPanel.setSize(frame.getWidth(), actionBarPanel.getHeight());
        Dimension screenSize = SwingConstants.getScreenSize();
        if (frame.getWidth() < screenSize.width || frame.getHeight() < screenSize.getHeight()) {
            mainBar.canMidScreen(false);
        } else {
            mainBar.canMidScreen(true);
        }
        mainBar.resize();
    }

    /**
     * 窗口是否可以缩放
     *
     * @return true：可以，否则不可以
     */
    public boolean isResizeable() {
        return resizeable;
    }

    /**
     * 设置是否允许窗口缩放
     *
     * @param resizeable 是否允许，true：可以缩放，否则不允许
     */
    public void setResizeable(boolean resizeable) {
        this.resizeable = resizeable;
    }

    /**
     * 重置窗体大小
     *
     * @param width  新的窗体宽度
     * @param height 新的窗体高度
     */
    public void resetSize(int width, int height) {
        if (frame == null) return;
        frame.setSize(width, height);
        resetSize();
    }

    /**
     * 重置窗体大小
     */
    private void resetSize() {
        resetActionBar();
        try {
            // 持续缩放窗口将不会进行立刻刷新，只有当间隔时间超过25ms才会全局刷新
            if (resizeFuture != null) resizeFuture.cancel(true);
            resizeFuture = resizeExecutor.schedule(resizeTask, 25, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Runnable resizeTask = () -> {
        getMainView().getComponent().setLocation(0, actionBarPanel.getHeight());
        getMainView().getComponent().setSize(frame.getWidth(), frame.getHeight() - actionBarPanel.getHeight());
        invalidate();
        defaultActivityResizeMouseListener.resetFrameBoundRect();
        /* add by HLM on 2020/7/26 解决显示中的下拉框由于窗口大小拉伸而跟随移动的功能 */
        if (View.mShowingSpinner != null) {
            View.mShowingSpinner.refreshLocation();
        }
        if (PopupWindow.mShowingPopupWindow != null) {
            PopupWindow.mShowingPopupWindow.refreshLocation();
        }
    };

    /**
     * 重置窗口位置
     *
     * @param x 横坐标
     * @param y 纵坐标
     */
    public void resetLocation(int x, int y) {
        if (frame == null) return;
        frame.setLocation(x, y);
        setLocation(new Location(frame.getLocation().x, frame.getLocation().y));
        /* add by HLM on 2020/7/26 解决显示中的下拉框跟随窗口移动的功能 */
        if (View.mShowingSpinner != null) {
            View.mShowingSpinner.refreshLocation();
        }
        if (PopupWindow.mShowingPopupWindow != null) {
            PopupWindow.mShowingPopupWindow.refreshLocation();
        }
        /* add by HLM on 2020/8/29 解决显示中的dialog跟随窗口移动的功能 */
        if (DialogManager.getTopDialog() != null) {
            DialogManager.getTopDialog().resetDialogWindowLocation();
        }
    }

    /**
     * 获取窗口的主容器
     *
     * @return 当前窗口的容器
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * 设置窗口缩放的监听
     *
     * @param resizeListener 窗口缩放监听接口
     */
    public void addOnActivityResizeListener(OnActivityResizeListener resizeListener) {
        this.resizeListener = resizeListener;
        defaultActivityResizeMouseListener.setOnActivityResizeListener(resizeListener);
    }

    /**
     * 设置窗口的布局
     *
     * @param resName 窗口布局的xml文件（eg. activity_main.xml）
     */
    public void setContentView(String resName) {
        LayoutAttribute attribute = new LayoutAttribute();
        attribute.setWidth(LayoutAttribute.MATCH_PARENT);
        attribute.setHeight(LayoutAttribute.MATCH_PARENT);
        getMainView().getComponent().removeAll();
        getMainView().setAttribute(attribute);
        // changed by HLM 解决窗体边框的颜色设置，通过缩小容器1个像素的大小，用来设置边框，边框的颜色为默认属性的border颜色，用户可以自行配置
        getMainView().getComponent().setLocation(1, actionBarPanel.getHeight());
        getMainView().getComponent().setSize(frame.getWidth() - 2, frame.getHeight() - actionBarPanel.getHeight() - 1);
        getMainView().getComponent().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.decode(DefaultAttribute.defaultBorderColor)));
        getMainView().setLayoutManager(layoutManager);
        layoutManager.inflate(getMainView(), resName);
        layoutManager.invalidate();
        frame.getContentPane().add(getMainView().getComponent());
        onStart();
    }

    /**
     * 设置窗口的布局
     *
     * @param resAbsolutePath 窗口布局的xml文件的绝对路径（eg. C://activity_main.xml）
     */
    public void setContentViewByAbsolute(String resAbsolutePath) {
        LayoutAttribute attribute = new LayoutAttribute();
        attribute.setWidth(LayoutAttribute.MATCH_PARENT);
        attribute.setHeight(LayoutAttribute.MATCH_PARENT);
        getMainView().getComponent().removeAll();
        getMainView().setAttribute(attribute);
        // changed by HLM 解决窗体边框的颜色设置，通过缩小容器1个像素的大小，用来设置边框，边框的颜色为默认属性的border颜色，用户可以自行配置
        getMainView().getComponent().setLocation(100, actionBarPanel.getHeight());
        top.totoro.plugin.util.Log.d(this, String.format("width %d, height %d", getSize().width - 2, getSize().height - actionBarPanel.getHeight() - 1));
        getMainView().getComponent().setSize(getSize().width - 2, getSize().height - actionBarPanel.getHeight() - 1);
        getMainView().getComponent().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.decode(DefaultAttribute.defaultBorderColor)));
        getMainView().setLayoutManager(layoutManager);
        layoutManager.inflateByAbsolutePath(getMainView(), resAbsolutePath);
        layoutManager.invalidate();
        getMainView().setBackgroundColor(JBColor.BLACK);
    }

    /**
     * 点击了返回按钮
     */
    @Override
    public void onBackClick() {
        if (parentActivity == null) return;
        // 只有存在父窗口时才能够被设置为可返回的
        AnimateUtil.transparentOut(this, 0.5f, () -> {
            onStop();
            // 需要重新显示父窗口
            parentActivity.onStart();
            ActivityManager.setTopActivity(parentActivity);
            AnimateUtil.transparentIn(parentActivity, 0.75f);
        });
    }

    /**
     * 窗体最小化
     */
    @Override
    public void onMinClick() {
        /* add by HLM on 2020/7/27 解决显示中的下拉框在窗体最小化时的隐藏功能 */
        if (View.mShowingSpinner != null) {
            View.mShowingSpinner.dismiss();
        }
        if (PopupWindow.mShowingPopupWindow != null) {
            PopupWindow.mShowingPopupWindow.dismiss();
        }
        frame.setExtendedState(JFrame.ICONIFIED);
    }

    /**
     * 窗体恢复大小
     */
    @Override
    public void onMidClick() {
        if (normalLocation != null) {
            resetLocation(normalLocation.xOnParent, normalLocation.yOnScreen);
        }
        if (normalSize != null) {
            resetSize(normalSize.width, normalSize.height);
        }
    }

    /**
     * 窗体最大化
     */
    @Override
    public void onMaxClick() {
        normalSize = new Size(frame.getSize().width, frame.getSize().height);
        normalLocation = new Location(frame.getLocation().x, frame.getLocation().y);
        Dimension screen = SwingConstants.getScreenSize();
        resetLocation(0, 0);
        resetSize(screen.width, screen.height);
    }

    /**
     * 关闭窗口
     */
    @Override
    public void onCloseClick() {
        /* add by HLM on 2020/7/27 解决显示中的下拉框在窗体销毁时的隐藏功能 */
        if (View.mShowingSpinner != null) {
            View.mShowingSpinner.dismiss();
        }
        if (PopupWindow.mShowingPopupWindow != null) {
            PopupWindow.mShowingPopupWindow.dismiss();
        }
        finish();
    }

    /**
     * 窗体拖拽
     *
     * @param start 鼠标开始按住的位置
     * @param x     鼠标当前的x坐标
     * @param y     鼠标当前的y坐标
     */
    @Override
    public void onActivityDrag(Point start, int x, int y) {
        resetLocation(frame.getX() - start.x + x, frame.getY() - start.y + y);
    }

    /**
     * 标题栏的大小发生改变
     */
    @Override
    public void onActionBarResize() {
        resetSize();
    }

    /************************* 启动后台服务 add on 2020/09/19 *************************/

    /**
     * 独立模式启动后台服务
     *
     * @param intent 具体的服务意图，指向调用服务的上下文和一个具体服务
     */
    public void startService(Intent intent) {
        Service service = ((Service) intent.getTargetContext());
        service.startService(intent);
        ServiceManager.putStartedService(intent.getCurrentContext(), service);
    }

    /**
     * 由当前activity作为上下文，以独立模式启动后台服务
     *
     * @param service 要启动的后台服务
     */
    public void startService(Service service) {
        Intent intent = new Intent(this, service.getClass());
        startService(intent);
    }

    /**
     * 由当前activity作为上下文，以独立模式启动后台服务
     *
     * @param targetServicePackage 服务对象具体的类路径
     */
    public void startService(String targetServicePackage) {
        Intent intent = new Intent(targetServicePackage);
        intent.setCurrentContext(this);
        startService(intent);
    }

    /**
     * 停止通过start启动的服务
     *
     * @param intent 要停止具体服务意图，指向调用服务的上下文和一个具体服务
     */
    public void stopService(Intent intent) {
        stopService((Service) intent.getTargetContext());
    }

    /**
     * 停止通过start启动的服务
     *
     * @param service 要停止的后台服务
     */
    public void stopService(Service service) {
        service.stopService();
    }

    /**
     * 绑定模式启动后台服务
     *
     * @param intent 具体的服务意图，指向调用服务的上下文和一个具体服务
     */
    public void bindService(Intent intent) {
        Service service = ((Service) intent.getTargetContext());
        if (service.isBinding()) {
            Log.e(this, "bindService had bound, please unbind first");
        } else {
            service.bindService(intent);
            ServiceManager.putBoundService(intent.getCurrentContext(), service);
        }
    }

    /**
     * 由当前activity作为上下文，以绑定模式启动后台服务
     *
     * @param service 要绑定的后台服务
     */
    public Intent bindService(Service service) {
        Intent intent = new Intent(this, service.getClass());
        bindService(intent);
        return intent;
    }

    /**
     * 由当前activity作为上下文，以绑定模式启动后台服务
     *
     * @param targetServicePackage 服务对象具体的类路径
     */
    public Intent bindService(String targetServicePackage) {
        Intent intent = new Intent(this, targetServicePackage);
        bindService(intent);
        return intent;
    }

    /**
     * 解除由bind启动的绑定服务
     *
     * @param intent 绑定的服务意图，指向调用服务的上下文和一个具体服务
     */
    public void unbindService(Intent intent) {
        unbindService(((Service) intent.getTargetContext()));
    }

    /**
     * 解除由bind启动的绑定服务
     *
     * @param service 被绑定的后台服务
     */
    public void unbindService(Service service) {
        if (service.isBinding()) {
            service.stopService();
        } else {
            Log.e(this, "unbindService never bound");
        }
    }

}
