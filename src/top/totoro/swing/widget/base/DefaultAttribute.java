package top.totoro.swing.widget.base;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import top.totoro.swing.widget.util.AttributeDefaultValue;
import top.totoro.swing.widget.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public final class DefaultAttribute {
    private static final String DEFAULT_ATTRIBUTE_CONFIG_FILE = "values/styles.swing";
    public static String defaultBackgroundColor = AttributeDefaultValue.WHITE_COLOR;
    public static String defaultBorderColor = "#dbdbdb";
    public static String defaultThemeColor = AttributeDefaultValue.WHITE_COLOR;
    public static String appIcon = "img/swing_logo.png";

    private static boolean hadLoad = false; // 避免多次加载资源

    /**
     * 加载应用的默认属性，给布局控件使用
     *
     * @param app 要加载的默认属性的应用
     */
    public static void loadDefaultAttribute(Class<?> app) {
        if (hadLoad) return;
        hadLoad = true;
        URL url = app.getClassLoader().getResource(DEFAULT_ATTRIBUTE_CONFIG_FILE);
        if (url == null) {
            Log.d("DefaultAttribute", "no exist default resource file");
            return;
        }
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(url);
            Element root = document.getRootElement();
            if (root == null) return;
            Element defaultElement = root.getChild("default");
            List<Element> elements = defaultElement.getChildren();
            for (Object element : elements) {
                if (element instanceof Element) {
                    List<Element> attributes = ((Element) element).getChildren();
                    for (Object attribute : attributes) {
                        if (attribute instanceof Attribute) {
                            String name = ((Attribute) attribute).getValue();
                            String value = ((Element) element).getText();
                            if (value == null || "".equals(value)) continue;
                            switch (name) {
                                case "backgroundColor":
                                    defaultBackgroundColor = getColor(defaultBackgroundColor, value);
                                    break;
                                case "borderColor":
                                    defaultBorderColor = getColor(defaultBorderColor, value);
                                    break;
                                case "themeColor":
                                    defaultThemeColor = getColor(defaultThemeColor, value);
                                    break;
                                case "appIcon":
                                    appIcon = value;
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
            // 加载出错代表还没完全加载
            hadLoad = false;
        }
    }

    /**
     * 获取正确的颜色
     *
     * @param defaultColor 如果颜色值不正确，返回默认颜色
     * @param value        颜色值
     * @return 颜色
     */
    private static String getColor(String defaultColor, String value) {
        if (BaseAttribute.isColor(value)) {
            return value;
        }
        return defaultColor;
    }
}
