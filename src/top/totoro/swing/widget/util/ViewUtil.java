package top.totoro.swing.widget.util;

import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.exception.LayoutException;
import top.totoro.swing.widget.view.View;

import java.lang.reflect.InvocationTargetException;

public class ViewUtil {

    private static final String VIEW_PACKAGE = "top.totoro.swing.widget.view.";

    public static <T extends View> T createView(View parent, String viewName, ViewAttribute attribute) {
        Class clazz;
        try {
            if (viewName == null) {
                throw new LayoutException(attribute.getResName() + "文件中View节点解析错误。");
            } else if (viewName.contains(".")) {
                clazz = Class.forName(viewName);
            } else {
                clazz = Class.forName(VIEW_PACKAGE + viewName);
            }
            T layout = (T) clazz.getConstructor(View.class).newInstance(parent);
            if (layout instanceof BaseLayout) {
                return layout;
            }
            layout.setAttribute(attribute);
            return layout;
        } catch (LayoutException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            if (e instanceof ClassNotFoundException) {
                new LayoutException(attribute.getResName() + "文件中的 " + viewName + " 不存在。").printStackTrace();
                return null;
            }
            e.printStackTrace();
        }
        return null;
    }
}
