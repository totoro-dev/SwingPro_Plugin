package top.totoro.swing.widget.base;

import org.jdom.Attribute;
import org.jdom.Element;
import top.totoro.swing.widget.exception.AttributeException;
import top.totoro.swing.widget.util.AttributeDefaultValue;

import java.awt.*;
import java.net.URL;

public class BaseAttribute {
    /* 布局节点元素，不同的视图可以自己处理自己想要的属性 */
    private Element element;

    private String resName = "";
    private String nodeName = "";

    public static final int OPAQUE = 0; // 不透明
    public static final int NOT_OPAQUE = 1; // 透明

    // 组件自适应
    public static final int MATCH_PARENT = -2;
    public static final int WRAP_CONTENT = -1;
    // 组件是否可见
    public static final int GONE = 0;
    public static final int VISIBLE = 1;

    /* change by HLM on 2020/7/27 解决没有主动设置id时报错 */
    private String id = null;
    /* change end */
    private int opaque = OPAQUE; // 背景是否透明，默认不透明
    private int startX = 0;
    private int startY = 0;
    private int width = 0;
    private int height = 0;
    private int visible = VISIBLE;

    private String background = DefaultAttribute.defaultBackgroundColor;
    private String src = "";

    /* add by HLM on 2020/7/27 增加边框设置 */
    public String borderColor = DefaultAttribute.defaultBorderColor;
    private int topBorder = 0;
    private int bottomBorder = 0;
    private int leftBorder = 0;
    private int rightBorder = 0;
    /* add end */

    /* add by HLM on 2020/9/27 增加偏移设置*/
    private int margin = 0;
    private int marginLeft = 0;
    private int marginRight = 0;
    private int marginTop = 0;
    private int marginBottom = 0;
    /* add end */

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public Color getBackground() {
        return Color.decode(background);
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public Color getBorderColor() {
        return Color.decode(borderColor);
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public int getTopBorder() {
        return topBorder;
    }

    public void setTopBorder(int topBorder) {
        this.topBorder = topBorder;
    }

    public int getBottomBorder() {
        return bottomBorder;
    }

    public void setBottomBorder(int bottomBorder) {
        this.bottomBorder = bottomBorder;
    }

    public int getLeftBorder() {
        return leftBorder;
    }

    public void setLeftBorder(int leftBorder) {
        this.leftBorder = leftBorder;
    }

    public int getRightBorder() {
        return rightBorder;
    }

    public void setRightBorder(int rightBorder) {
        this.rightBorder = rightBorder;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
        setMarginLeft(margin);
        setMarginRight(margin);
        setMarginTop(margin);
        setMarginBottom(margin);
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    /**
     * 检测界面元素是否指定了有效的高度属性，每个元素都必须指定高度
     *
     * @param attr 高度属性
     * @return 存在为true，否则为false
     */
    public boolean checkHeightValue(Attribute attr) {
        try {
            if (attr == null) {
                throw new AttributeException("组件" + nodeName + "必须指定height属性");
            }
            String value = attr.getValue();
            if (value == null) {
                throw new AttributeException("组件" + nodeName + "必须指定height属性");
            }
            if (value.equals(AttributeDefaultValue.MATCH_PARENT)) {
                setHeight(top.totoro.swing.widget.base.BaseAttribute.MATCH_PARENT);
            } else if (value.equals(AttributeDefaultValue.WRAP_CONTENT)) {
                setHeight(top.totoro.swing.widget.base.BaseAttribute.WRAP_CONTENT);
            } else if (isUnsignedInt(value)) {
                setHeight(Integer.parseInt(value));
            } else {
                throw new AttributeException("组件" + nodeName + "的height属性必须是 : 正整数、match_parent、wrap_content");
            }
        } catch (AttributeException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 检测界面元素是否指定了有效的宽度属性，每个元素都必须指定宽度
     *
     * @param attr 宽度属性
     * @return 存在为true，否则为false
     */
    public boolean checkWidthValue(Attribute attr) {
        try {
            if (attr == null) {
                throw new AttributeException("组件" + nodeName + "必须指定width属性");
            }
            String value = attr.getValue();
            if (value == null) {
                throw new AttributeException("组件" + nodeName + "必须指定width属性");
            }
            if (value.equals(AttributeDefaultValue.MATCH_PARENT)) {
                setWidth(top.totoro.swing.widget.base.BaseAttribute.MATCH_PARENT);
            } else if (value.equals(AttributeDefaultValue.WRAP_CONTENT)) {
                setWidth(top.totoro.swing.widget.base.BaseAttribute.WRAP_CONTENT);
            } else if (isUnsignedInt(value)) {
                setWidth(Integer.parseInt(value));
            } else {
                throw new AttributeException("组件" + nodeName + "的width属性必须是 : 正整数、match_parent、wrap_content");
            }
        } catch (AttributeException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 判断字符串是否是一个无符号的整数
     *
     * @param value 字符串
     * @return 是整数为true，否则为false
     */
    public boolean isUnsignedInt(String value) {
        if (value == null || value.length() == 0) return false;
        char[] cs = value.toCharArray();
        for (char c : cs) {
            int ascii = Integer.parseInt(Integer.toString(c));
            if (ascii >= 48 && ascii <= 57) {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否是颜色值
     *
     * @param value 字符串
     * @return 是颜色为true，否则为false
     */
    public static boolean isColor(String value) {
        if (value != null && value.startsWith("#") && (value.length() == 4 || value.length() == 7 || value.length() == 9)) {
            char[] cs = value.substring(1).toCharArray();
            for (char c : cs) {
                int ascii = Integer.parseInt(Integer.toString(c));
                if ((ascii >= 48 && ascii <= 57) || (ascii >= 65 && ascii <= 70) || (ascii >= 97 && ascii <= 102)) {
                    continue;
                }
                return false;
            }
        } else return false;
        return true;
    }

    /**
     * 判断字符串是否是一个正确的资源路径
     *
     * @param value 字符串
     * @return 资源存在为true，否则为false
     */
    public boolean isSrcPath(String value) {
        if (value == null || value.length() == 0) return false;
        URL url = getClass().getClassLoader().getResource(value);
        return url != null;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String toString() {
        return "BaseAttribute{" +
                "resName='" + resName + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", id='" + id + '\'' +
                ", opaque=" + opaque +
                ", startX=" + startX +
                ", startY=" + startY +
                ", width=" + width +
                ", height=" + height +
                ", visible=" + visible +
                ", background='" + background + '\'' +
                ", src='" + src + '\'' +
                '}';
    }
}
