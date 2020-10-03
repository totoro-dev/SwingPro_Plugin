package top.totoro.swing.widget.manager;

import top.totoro.swing.widget.context.Context;
import top.totoro.swing.widget.context.Intent;
import top.totoro.swing.widget.context.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用同步锁来处理服务的同步和记录、移除
 */
public class ServiceManager {

    private static final String TAG = top.totoro.swing.widget.manager.ServiceManager.class.getSimpleName();

    // 使用startService启动的服务
    private static final Map<Context, List<Service>> mStartedServices = new HashMap<>();
    // 使用bindService启动的服务
    private static final Map<Context, List<Service>> mBoundServices = new HashMap<>();

    private static final Lock mLock = new ReentrantLock();

    /**
     * 记录一个通过 {@link Service#startService(Intent)} 启动的后台服务
     *
     * @param context 服务的启动者
     * @param service 需要记录的服务
     */
    public static void putStartedService(Context context, Service service) {
        try {
            mLock.lock();
            if (mStartedServices.computeIfAbsent(context, key -> new ArrayList<>()).contains(service)) return;
            mStartedServices.computeIfAbsent(context, key -> new ArrayList<>()).add(service);
        } finally {
            mLock.unlock();
        }
    }

    /**
     * 记录一个通过 {@link Service#bindService(Intent)} 启动的后台服务
     *
     * @param context 服务的启动者
     * @param service 需要记录的服务
     */
    public static void putBoundService(Context context, Service service) {
        try {
            mLock.lock();
            if (mBoundServices.computeIfAbsent(context, key -> new ArrayList<>()).contains(service)) return;
            mBoundServices.computeIfAbsent(context, key -> new ArrayList<>()).add(service);
        } finally {
            mLock.unlock();
        }
    }

    /**
     * 移除一个通过 {@link Service#startService(Intent)} 启动的后台服务
     *
     * @param context 服务的启动者
     * @param service 需要记录的服务
     * @return 是否存在该服务
     */
    public static boolean removeStartedService(Context context, Service service) {
        try {
            mLock.lock();
            List<Service> list = mStartedServices.get(context);
            if (list != null) {
                list.remove(service);
                if (list.size() == 0) {
                    mStartedServices.remove(context);
                }
                return true;
            }
            return false;
        } finally {
            mLock.unlock();
        }
    }

    /**
     * 移除一个通过 {@link Service#bindService(Intent)} 启动的后台服务
     *
     * @param context 服务的启动者
     * @param service 需要记录的服务
     * @return 是否存在该服务
     */
    public static boolean removeBoundService(Context context, Service service) {
        try {
            mLock.lock();
            List<Service> list = mBoundServices.get(context);
            if (list != null) {
                list.remove(service);
                if (list.size() == 0) {
                    mBoundServices.remove(context);
                }
                return true;
            }
            return false;
        } finally {
            mLock.unlock();
        }
    }

    /**
     * 获取所有通过 {@link Service#startService(Intent)} 启动的后台服务
     *
     * @param context 服务的启动者
     * @return 记录的后台服务
     */
    public static List<Service> getStartedServices(Context context) {
        try {
            mLock.lock();
            return new ArrayList<>(mStartedServices.computeIfAbsent(context, key -> new ArrayList<>()));
        } finally {
            mLock.unlock();
        }
    }

    /**
     * 获取所有通过 {@link Service#bindService(Intent)} 启动的后台服务
     *
     * @param context 服务的启动者
     * @return 记录的后台服务
     */
    public static List<Service> getBoundServices(Context context) {
        try {
            mLock.lock();
            return new ArrayList<>(mBoundServices.computeIfAbsent(context, key -> new ArrayList<>()));
        } finally {
            mLock.unlock();
        }
    }

    /**
     * 是否还存在通过 {@link Service#startService(Intent)} 启动的后台服务
     *
     * @return 存在为true，不存在为false
     */
    public static boolean isEmpty() {
        try {
            mLock.lock();
            return mStartedServices.isEmpty();
        } finally {
            mLock.unlock();
        }
    }
}
