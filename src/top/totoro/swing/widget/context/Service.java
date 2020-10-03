package top.totoro.swing.widget.context;

import top.totoro.swing.widget.manager.ActivityManager;
import top.totoro.swing.widget.manager.ServiceManager;
import top.totoro.swing.widget.util.Log;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class Service extends Context {

    // 为了不阻塞主线程，所有的后台服务需要进入线程池中执行相应的服务
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    public long mServiceId = 0L;
    private volatile boolean isBinding = false; // 是否是绑定状态
    private volatile boolean hasStarted = false; // 是否启动过，决定能否重新startService
    private volatile boolean hasBound = false; // 曾经绑定过，决定是否reBind
    private Intent mIntent;
    private Frame mFrame; // 如果是startService启动的话需要创建frame用来防止应用退出

    // 使用不同的Future来支持同个服务可通过不同的方式启动，且互不干扰
    private Future<?> mStartFuture; // 通过startService启动的服务
    private Future<?> mBindFuture; // 通过bindService启动的服务

    public abstract void onBind(Intent intent);

    /**
     * 当前服务是否被bindService启动
     *
     * @return 如果使用bindService启动，且未使用stopService则为true，若被stop则为false；
     * 如果没有被bindService启动，则为false。
     */
    public boolean isBinding() {
        return isBinding;
    }

    /**
     * 以独立的方式开启后台服务，如果不使用stopService退出的话则一直保持运行
     *
     * @param intent 服务的意图，指明是谁启动的服务
     */
    public void startService(Intent intent) {
        if (hasStarted) {
            Log.e(this, "startService has started, can not start again");
        } else {
            if (mFrame == null) {
                mFrame = new Frame();
                mFrame.setSize(0, 0);
                mFrame.setUndecorated(true);
                mFrame.setVisible(true);
                mFrame.setVisible(false);
            }
            mStartFuture = EXECUTOR_SERVICE.submit(() -> onStartCommand(intent));
            hasStarted = true;
        }
    }

    /**
     * 以绑定的方式启动后台服务，会自动跟随启动者的生命周期
     *
     * @param intent 服务的意图，指明服务的启动者
     */
    public void bindService(Intent intent) {
        if (isBinding) {
            Log.e(this, "bindService has bound, can not bind again");
            return;
        } else {
            isBinding = true;
        }
        mBindFuture = EXECUTOR_SERVICE.submit(() -> {
            onBind(intent);
            if (!hasBound) {
                hasBound = true;
                onRebind(intent);
            }
            onStartCommand(intent);
        });
    }

    /**
     * 说明服务已经配置成功，开始正式执行任务
     *
     * @param intent 服务的意图
     */
    public void onStartCommand(Intent intent) {
        this.mIntent = intent;
        onStart(intent);
    }

    private void onStart(Intent intent) {

    }

    /**
     * 如果服务由bindService启动，在stopService停止服务的时候会先解绑服务
     *
     * @param intent 当前服务的意图
     */
    public void onUnbind(Intent intent) {

    }

    /**
     * 如果重复的通过bindService启动服务，在第二次bind的时候会主动触发reBind
     *
     * @param intent 当前服务的意图
     */
    public void onRebind(Intent intent) {

    }

    /**
     * 停止已经开启的后台服务，
     * 但不会停止服务中正在执行的子线程任务。
     * 当应用退出的时候如果子线程中的任务还没执行完毕，
     * 则会被强制退出。
     */
    public void stopService() {
        if (isBinding) {
            onUnbind(mIntent);
            if (mBindFuture != null) {
                mBindFuture.cancel(true);
                mBindFuture = null;
            }
            ServiceManager.removeBoundService(mIntent.getCurrentContext(), this);
            isBinding = false;
        }
        if (hasStarted) {
            if (mStartFuture != null) {
                mStartFuture.cancel(true);
                mStartFuture = null;
            }
            ServiceManager.removeStartedService(mIntent.getCurrentContext(), this);
            hasStarted = false;
        }
        onDestroy();
        if (ServiceManager.isEmpty() /* 没有后台服务了 */
                && ActivityManager.getTopActivity() == null /* 没有前台活动了 */) {
            Log.e(this, "system exit for service");
            System.exit(0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFrame != null) mFrame.dispose();
    }
}
