package top.totoro.swing.widget.context;

import top.totoro.swing.widget.util.Log;

/**
 * 动作意图的封装
 */
public class Intent {

    private Context currentContext;
    private Context targetContext;
    private Class<? extends Context> targetContextClass;

    public Intent() {
    }

    public Intent(String targetContextPackage) {
        Class<?> targetContext = null;
        try {
            targetContext = Class.forName(targetContextPackage);
            Log.d(this, "targetContext super class  = " + targetContext.getSuperclass().getSuperclass().getSimpleName());
            if (targetContext.getSuperclass().getSuperclass().getSimpleName().equals(Context.class.getSimpleName())) {
                //noinspection unchecked
                this.targetContextClass = (Class<? extends Context>) targetContext;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Intent(Context currentContext, String targetContextPackage) {
        this(targetContextPackage);
        this.currentContext = currentContext;
    }

    public Intent(Context currentContext, Class<? extends Context> targetContextClass) {
        this.currentContext = currentContext;
        this.targetContextClass = targetContextClass;
    }

    /**
     * 设置意图的当前上下文
     *
     * @param currentContext 当前上下文
     */
    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    /**
     * 设置意图目标上下文
     *
     * @param targetContextClass 目标上下文的包路径（包括类名）
     */
    public void setTargetContextClass(Class<? extends Context> targetContextClass) {
        this.targetContextClass = targetContextClass;
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public Class<? extends Context> getTargetContextClass() {
        return targetContextClass;
    }

    public synchronized Context getTargetContext() {
        if (targetContext == null) {
            synchronized (Intent.class) {
                if (targetContext == null) {
                    try {
                        targetContext = targetContextClass.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return targetContext;
    }
}
