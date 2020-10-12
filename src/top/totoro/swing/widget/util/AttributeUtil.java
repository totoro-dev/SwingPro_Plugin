package top.totoro.swing.widget.util;

import org.jdom.Attribute;
import org.jdom.Element;
import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.exception.AttributeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static top.totoro.swing.widget.util.AttributeKey.*;

public class AttributeUtil {

    public static LayoutAttribute getLayoutAttribute(String resName, Element layout, boolean initNecessaryAttr) {
        String nodeName = layout.getName();
        LayoutAttribute layoutAttribute = new LayoutAttribute();
        layoutAttribute.setElement(layout);
        // 验证属性中必须包含正确的height和width属性
        if (initialNecessaryAttribute(layoutAttribute, resName, nodeName, layout, initNecessaryAttr)) {
            List<?> attrs = layout.getAttributes();
            for (Object object : attrs) {
                Attribute attr;
                if (object instanceof Attribute) attr = (Attribute) object;
                else continue;
                String name = attr.getName();
                switch (name) {
                    case HEIGHT:
                    case WIDTH:
                        continue;
                    case ID:
                    case GRAVITY:
                        invokeSet(name, attr.getValue(), String.class, layoutAttribute);
                        continue;
                    case ORIENTATION:
                        if (attr.getValue().equals(AttributeDefaultValue.VERTICAL)) {
                            invokeSet(name, LayoutAttribute.VERTICAL, int.class, layoutAttribute);
                            continue;
                        } else if (attr.getValue().equals(AttributeDefaultValue.HORIZONTAL)) {
                            invokeSet(name, LayoutAttribute.HORIZONTAL, int.class, layoutAttribute);
                            continue;
                        }
                        break;
                }
                getBaseAttribute(name, attr, layoutAttribute);
            }
        }
        return layoutAttribute;
    }

    public static ViewAttribute getViewAttribute(String resName, Element view, boolean initNecessaryAttr) {
        String nodeName = view.getName();
        ViewAttribute viewAttribute = new ViewAttribute();
        // add by HLM on 2020/7/26 解决不同视图可以自定义属性功能，避免ViewAttribute太过冗余 */
        viewAttribute.setElement(view);
        // add end
        // 验证属性中必须包含正确的height和width属性
        if (initialNecessaryAttribute(viewAttribute, resName, nodeName, view, initNecessaryAttr)) {
            List<?> attrs = view.getAttributes();
            for (Object object : attrs) {
                Attribute attr;
                if (object instanceof Attribute) attr = (Attribute) object;
                else continue;
                String name = attr.getName();
                switch (name) {
                    case HEIGHT:
                    case WIDTH:
                        continue;
                    case ID:
                    case TEXT:
                    case TEXT_STYLE:
                    case TEXT_FONT:
                    case TEXT_COLOR:
                    case HINT_TEXT:
                        invokeSet(name, attr.getValue(), String.class, viewAttribute);
                        continue;
                    case TEXT_SIZE:
                        invokeSet(name, Integer.parseInt(attr.getValue()), int.class, viewAttribute);
                        continue;
                }
                getBaseAttribute(name, attr, viewAttribute);
            }
        }
        return viewAttribute;
    }

    /**
     * 初始化一个View必要的属性，如果未配置这些属性将无法继续，并报错
     * 这里指宽和高两个必要属性
     *
     * @param baseAttribute     这个View持有的基本属性Bean
     * @param resName           所在的xml文件
     * @param nodeName          View节点的名字
     * @param ele               节点的Element对象
     * @param initNecessaryAttr
     * @return 是否初始化成功
     */
    private static boolean initialNecessaryAttribute(BaseAttribute baseAttribute, String resName, String nodeName, Element ele, boolean initNecessaryAttr) {
        baseAttribute.setResName(resName);
        baseAttribute.setNodeName(nodeName);
        Attribute height = ele.getAttribute(HEIGHT);
        Attribute width = ele.getAttribute(WIDTH);
        // add by HLM on 2020/9/25 对于布局中在GridLayout内的元素不需要指定width、height等属性
        if (!initNecessaryAttr) {
            if (width != null) {
                String value = width.getValue();
                if (value.equals(AttributeDefaultValue.MATCH_PARENT)) {
                    baseAttribute.setWidth(BaseAttribute.MATCH_PARENT);
                } else if (value.equals(AttributeDefaultValue.WRAP_CONTENT)) {
                    baseAttribute.setWidth(BaseAttribute.WRAP_CONTENT);
                } else if (baseAttribute.isUnsignedInt(value)) {
                    baseAttribute.setWidth(Integer.parseInt(value));
                }
            } else {
                // 布局中没有指定必要的width属性，那么这里需要为其指定默认的值
                baseAttribute.setWidth(BaseAttribute.MATCH_PARENT);
            }
            if (height != null) {
                String value = height.getValue();
                if (value.equals(AttributeDefaultValue.MATCH_PARENT)) {
                    baseAttribute.setHeight(BaseAttribute.MATCH_PARENT);
                } else if (value.equals(AttributeDefaultValue.WRAP_CONTENT)) {
                    baseAttribute.setHeight(BaseAttribute.WRAP_CONTENT);
                } else if (baseAttribute.isUnsignedInt(value)) {
                    baseAttribute.setHeight(Integer.parseInt(value));
                }
            } else {
                // 布局中没有指定必要的height属性，那么这里需要为其指定默认的值
                baseAttribute.setHeight(BaseAttribute.MATCH_PARENT);
            }
            return true;
        }
        // add end
        return baseAttribute.checkHeightValue(height) && baseAttribute.checkWidthValue(width);

    }

    /**
     * 解析并初始化一个View节点所设置的所有基础属性
     *
     * @param name          View节点名字
     * @param attr          这个View的xml属性对象
     * @param baseAttribute 这个View持有的基本属性Bean
     */
    private static void getBaseAttribute(String name, Attribute attr, BaseAttribute baseAttribute) {
        try {
            // 验证属性值不为空
            if (attr.getValue().length() == 0) {
                throw AttributeException.getValueInvalid(baseAttribute, name, "", "必须赋值");
            }
            /* add by HLM on 2020/7/27 排除各个自定义控件的属性检查 */
            switch (name) {
                case arrayAttrKey:
                case enterColorKey:
                case selectedColorKey:
                case switchOnKey:
                case switchOffKey:
                case isSwitchOnKey:
                case scaleType:
                case column:
                case TEXT_ALIGNMENT:
                case GAP:
                case GAP_VERTICAL:
                case GAP_HORIZONTAL:
                case selectedBoxIconKey:
                case unselectedBoxIconKey:
                case isSelectedOnKey:
                    /* 这些属性属于不同控件自有的，不需要在这里做初始化 */
                    return;
            }
            /* add end */
            boolean isInt = true;
            Object value = null;
            switch (attr.getValue()) {
                case AttributeDefaultValue.GONE:
                    value = BaseAttribute.GONE;
                    break;
                case AttributeDefaultValue.VISIBLE:
                    value = BaseAttribute.VISIBLE;
                    break;
                case AttributeDefaultValue.OPAQUE:
                    value = LayoutAttribute.OPAQUE;
                    break;
                case AttributeDefaultValue.NOT_OPAQUE:
                    value = LayoutAttribute.NOT_OPAQUE;
                    break;
                default:
                    if (baseAttribute.isUnsignedInt(attr.getValue())) {
                        value = Integer.parseInt(attr.getValue());
                    } else if (BaseAttribute.isColor(attr.getValue()) || baseAttribute.isSrcPath(attr.getValue())) {
                        isInt = false;
                        value = attr.getValue();
                    } else {
                        isInt = false;
                        // 判断是什么属性的错误
                        if (name.equals(BACKGROUND)) {
//                            throw AttributeException.getValueInvalid(baseAttribute, name, attr.getValue(), "必须是颜色值或图片资源");
                        } else if (name.equals(SRC)) {
                            value = attr.getValue();
//                            throw AttributeException.getValueInvalid(baseAttribute, name, attr.getValue(), "必须是颜色值或图片资源");
                        } else {
                            throw AttributeException.getValueInvalid(baseAttribute, name, attr.getValue(), "是非法的");
                        }
                    }
                    break;
            }
            // 判断属性类型是否为整型
            if (isInt) {
                invokeSet(name, value, int.class, baseAttribute);
            } else {
                if (value == null) value = "";
                invokeSet(name, value, String.class, baseAttribute);
            }
        } catch (AttributeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行Bean中对应属性的set方法
     *
     * @param name      属性名称
     * @param value     属性值
     * @param valueType 属性值类型
     * @param target    bean对象
     */
    private static void invokeSet(String name, Object value, Class<?> valueType, BaseAttribute target) {
        try {
            String firstLetter = String.valueOf(Character.toUpperCase(name.charAt(0)));
            Method set = target.getClass().getMethod("set" + firstLetter + name.substring(1), valueType);
            set.invoke(target, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            if (e instanceof NoSuchMethodException) {
                AttributeException.getAttrNameInvalid(target, name).printStackTrace();
            } else {
                AttributeException.getValueInvalid(target, name, String.valueOf(value), "是非法的").printStackTrace();
            }
        }
    }

}
