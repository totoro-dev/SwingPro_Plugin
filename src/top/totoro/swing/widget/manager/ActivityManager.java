package top.totoro.swing.widget.manager;

import top.totoro.swing.widget.base.Location;
import top.totoro.swing.widget.base.Size;
import top.totoro.swing.widget.context.Activity;
import top.totoro.swing.widget.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActivityManager {
    private static final String TAG = top.totoro.swing.widget.manager.ActivityManager.class.getSimpleName();
    private static Activity mTopActivity = null;
    private static final Map<Class<? extends Activity>, Object> CREATED_ACTIVITY = new ConcurrentHashMap<>();

    /**
     * 设置当前的顶层窗口，但是新的窗口必须不为空，不然新的窗口不会被置为顶层窗口
     *
     * @param activity 新的窗口
     */
    public static void setTopActivity(Activity activity) {
//        if (activity == null) return;
        mTopActivity = activity;
    }

    /**
     * 获取顶层窗口
     *
     * @return 当前应用最顶层的窗口
     */
    public static Activity getTopActivity() {
        return mTopActivity;
    }

    public static Map<Class<? extends Activity>, Object> getCreatedActivities() {
        return CREATED_ACTIVITY;
    }

    /**
     * 应用退出时关闭所有缓存记录了的Activity
     *
     * @param now 当前触发退出应用的activity
     */
    public static void finishAll(Activity now) {
        Map<Class<? extends Activity>, Object> copy = new HashMap<>(CREATED_ACTIVITY);
        int CREATED_ACTIVITY_SIZE = CREATED_ACTIVITY.size(); // 只有第一次调用的时候是完整的记录
        CREATED_ACTIVITY.clear(); // 防止后面其它Activity的finish方法多次触发
        copy.forEach((aClass, obj) -> {
            // 如果是当前的这个activity就不调用其finish了
            if (obj instanceof Activity && obj != now) {
                ((Activity) obj).finish();
            }
        });

        if ((CREATED_ACTIVITY_SIZE == 1 /* 只有一个现存的Activity，直接退出 */
                || copy.size() == 0 /* 已经全部finish，可以退出 */)
                && ServiceManager.isEmpty() /* 没有驻留的后台服务了 */) {
            Log.e(TAG, "system exit for activity");
            System.exit(0);
        }
    }

    /**
     * 通过该方法可以获取到当前应用存在的窗口对象（Activity）
     * 如果窗口还没有被创建过的话，会根据提供的target，创建一个对应类型的窗口对象
     * 并将窗口对象添加到CREATED_ACTIVITY中。
     *
     * @param target 目标窗口的类
     * @param <A>    要获取的窗口的类型定义
     * @return 匹配target类型的窗口对象
     */
    @SuppressWarnings("unchecked")
    public static <A extends Activity> A getOrCreateActivity(Class<A> target) {
        AtomicBoolean isNewActivity = new AtomicBoolean(false);
        A targetActivity = (A) CREATED_ACTIVITY.computeIfAbsent(target, targetActivityType -> {
            Log.d(TAG, "getOrCreateActivity create a new activity type = " + target);
            isNewActivity.set(true);
            // CREATED_ACTIVITY中不存在target类型的窗口 需要重新创建一个窗口并添加到CREATED_ACTIVITY中。
            A activity = null;
            // 只负责窗体的创建，其余配置不在此处进行
            try {
                activity = target.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return activity;
        });
        Log.d(TAG, "get activity is " + targetActivity);
        if (targetActivity != null)
            targetActivity.setOnRestart(!isNewActivity.get());
        return targetActivity;
    }

    /**
     * 可以获取到指定大小的的窗口对象
     * 会通过CREATED_ACTIVITY查找是否存在同一类型的窗口，不存在则创建新的窗口
     * 如果窗口准备完成，则设置指定的大小
     *
     * @param target     目标窗口的类
     * @param targetSize 窗口的目标大小
     * @param <A>        要获取的窗口的类型定义
     * @return 一个具有指定大小的窗口
     */
    public static <A extends Activity> A getOrCreateActivityWithSize(Class<A> target, Size targetSize) {
        A targetActivity = getOrCreateActivity(target);
        if (targetActivity != null) {
            targetActivity.setSize(targetSize);
        }
        return targetActivity;
    }

    /**
     * 可以获取到指定位置的的窗口对象
     * 会通过CREATED_ACTIVITY查找是否存在同一类型的窗口，不存在则创建新的窗口
     * 如果窗口准备完成，则设置指定的位置
     *
     * @param target         目标窗口的类
     * @param targetLocation 窗口的目标位置
     * @param <A>            要获取的窗口的类型定义
     * @return 一个具有指定位置的窗口
     */
    public static <A extends Activity> A getOrCreateActivityWithLocation(Class<A> target, Location targetLocation) {
        A targetActivity = getOrCreateActivity(target);
        if (targetActivity != null) {
            targetActivity.setLocation(targetLocation);
        }
        return targetActivity;
    }

    /**
     * 可以获取到指定大小和位置的的窗口对象
     * 会通过CREATED_ACTIVITY查找是否存在同一类型的窗口，不存在则创建新的窗口
     * 如果窗口准备完成，则设置指定的大小和位置
     *
     * @param target         目标窗口的类
     * @param targetLocation 窗口的目标大小和位置
     * @param <A>            要获取的窗口的类型定义
     * @return 一个具有指定大小和位置的窗口
     */
    public static <A extends Activity> A getOrCreateActivityWithSizeAndLocation(Class<A> target, Size targetSize, Location targetLocation) {
        // 先处理窗口的大小
        A targetActivity = getOrCreateActivityWithSize(target, targetSize);
        if (targetActivity != null) {
            targetActivity.setLocation(targetLocation);
        }
        return targetActivity;
    }

    public static void removeActivity(Activity removeActivity) {
        CREATED_ACTIVITY.remove(removeActivity.getClass());
    }
}
