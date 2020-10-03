package top.totoro.swing.widget.util;

public class Log {
    public static void d(Object target, Object msg) {
//        if (target instanceof String) {
//            System.out.println(target + " : " + msg);
//        } else if (target instanceof Class) {
//            System.out.println(((Class) target).getSimpleName() + " : " + msg);
//        } else {
//            System.out.println(target.getClass().getSimpleName() + " : " + msg);
//        }
    }

    public static void e(Object target, Object error) {
        if (target instanceof String) {
            System.err.println(target + " : " + error);
        } else if (target instanceof Class) {
            System.err.println(((Class) target).getSimpleName() + " : " + error);
        } else {
            System.err.println(target.getClass().getSimpleName() + " : " + error);
        }
    }
}
