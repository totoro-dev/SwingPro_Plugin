package top.totoro.swing.widget.view;

import org.jdom.Attribute;
import org.jdom.Element;
import top.totoro.plugin.core.SimpleFileType;
import top.totoro.plugin.util.Log;
import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.util.AttributeKey;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Objects;

import static top.totoro.swing.widget.util.AttributeDefaultValue.*;

/**
 * 图片显示
 */
public class ImageView extends View<ViewAttribute, JPanel> {

    private final String TAG = getClass().getSimpleName();

    private final JLabel mImageContainer;
    private ImageIcon imageIcon;
    private int width = -3, height = -3;
    protected String scaleType = scaleFitCenter;

    public static final String FIT_CENTER = scaleFitCenter;
    public static final String CENTER = scaleCenter;
    public static final String FIT_XY = scaleFitXY;
    public static final String FIT_START = scaleFitStart;
    public static final String FIT_END = scaleFitEnd;

    public ImageView(View parent) {
        super(parent);
        component = new JPanel(null);
        mImageContainer = new JLabel("", JLabel.CENTER) {
            @Override
            public void paint(Graphics g) {
                switch (scaleType) {
                    default:
                    case scaleFitCenter:
                        fitCenter();
                    case scaleCenter:
                        // 居中显示
                        mImageContainer.setLocation((component.getWidth() - width) / 2, (component.getHeight() - height) / 2);
                        break;
                    case scaleFitXY:
                        fitXY(); // 拉伸
                        // 满控件显示
                        mImageContainer.setLocation(0, 0);
                        break;
                    case scaleFitStart:
                        fitCenter();
                        // 左上显示
                        mImageContainer.setLocation(0, 0);
                        break;
                    case scaleFitEnd:
                        fitCenter();
                        // 右下显示
                        mImageContainer.setLocation(component.getWidth() - width, component.getHeight() - height);
                        break;
                }
                super.paint(g);
            }
        };
        component.add(mImageContainer);
    }

    private void fitCenter() {
        if (attribute.getWidth() != BaseAttribute.WRAP_CONTENT
                || attribute.getHeight() != BaseAttribute.WRAP_CONTENT) {
            float widthScale = component.getWidth() / (float) width;
            float heightScale = component.getHeight() / (float) height;
            float scale = Math.min(widthScale, heightScale);
            if ((widthScale < 1 || heightScale < 1) && scale != 1) {
                Log.d(TAG, "scale = " + scale);
                width *= scale;
                height *= scale;
                imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
                mImageContainer.setSize(width, height);
            }
        }
    }

    /**
     * 设置新的图片资源
     *
     * @param src              图片资源路径
     * @param invalidateParent 是否全局刷新
     */
    public void setImage(String src, boolean invalidateParent) {
        int rootPathTailIndex = SimpleFileType.currentFile.lastIndexOf("src/main/resources/");
        if (rootPathTailIndex > 0) {
            String rootPath = SimpleFileType.currentFile.substring(0, rootPathTailIndex + "src/main/resources/".length());
            src = rootPath + src;
        }
        Log.d(this, "src = " + src);
        if (src == null || "".equals(src)) {
            Log.e(TAG, "为id为" + attribute.getId() + "的" + TAG + "设置背景图片时，图片路径不能为空");
            return;
        }
        File img = new File(src);
        if (img.exists()) {
            imageIcon = new ImageIcon(src);
        } else {
            Log.e(TAG, "为id为" + attribute.getId() + "的" + TAG + "设置背景图片时，图片路径不正确");
            imageIcon = null;
        }
        mImageContainer.setIcon(imageIcon);
        if (invalidateParent) {
            invalidateSuper();
        } else {
            reSizeAsImageSize();
        }
    }

    /**
     * 设置新的图片资源，并且全局刷新
     *
     * @param src 图片资源路径
     */
    public void setImage(String src) {
        setImage(src, true);
    }

    @Override
    public void setAttribute(ViewAttribute attribute) {
        super.setAttribute(attribute);
        // 背景透明
        component.setOpaque(false);
        // 获取设置的缩放类型
        Element element = attribute.getElement();
        Attribute scaleType = element.getAttribute(AttributeKey.scaleType);
        if (scaleType != null) {
            setScaleType(scaleType.getValue(), false);
        } else {
            setImage(attribute.getSrc(), false);
        }
    }

    /**
     * 设置图片显示时的缩放模式，并且全局刷新
     *
     * @param scaleType 缩放模式
     * @see top.totoro.swing.widget.view.ImageView#FIT_CENTER 居中等比例缩放，整个图片可见（默认模式）
     * @see top.totoro.swing.widget.view.ImageView#FIT_XY 拉伸宽高，填满整个视图
     * @see top.totoro.swing.widget.view.ImageView#FIT_START 从左上角开始显示图片，等比例缩放，整个图片可见（右下角可能有空隙）
     * @see top.totoro.swing.widget.view.ImageView#FIT_END 从右下角开始显示图片，等比例缩放，整个图片可见（左上角可能有空隙）
     * @see top.totoro.swing.widget.view.ImageView#CENTER 不拉伸图片，保持图片大小居中显示
     */
    public void setScaleType(String scaleType) {
        // 全局刷新
        setScaleType(scaleType, true);
    }

    /**
     * 设置图片显示时的缩放模式
     *
     * @param scaleType        缩放模式
     * @param invalidateParent 是否需要全局刷新
     * @see top.totoro.swing.widget.view.ImageView#FIT_CENTER 居中等比例缩放，整个图片可见（默认模式）
     * @see top.totoro.swing.widget.view.ImageView#FIT_XY 拉伸宽高，填满整个视图
     * @see top.totoro.swing.widget.view.ImageView#FIT_START 从左上角开始显示图片，等比例缩放，整个图片可见（右下角可能有空隙）
     * @see top.totoro.swing.widget.view.ImageView#FIT_END 从右下角开始显示图片，等比例缩放，整个图片可见（左上角可能有空隙）
     * @see top.totoro.swing.widget.view.ImageView#CENTER 不拉伸图片，保持图片大小居中显示
     */
    public void setScaleType(String scaleType, boolean invalidateParent) {
        if (!Objects.equals(this.scaleType, scaleType)) {
            this.scaleType = scaleType;
        }
        // 触发图片的刷新，并决定是否刷新父布局
        setImage(getAttribute().getSrc(), invalidateParent);
    }

    private void fitXY() {
        if (getAttribute().getWidth() != BaseAttribute.WRAP_CONTENT) {
            width = component.getWidth();
        }
        if (getAttribute().getHeight() != BaseAttribute.WRAP_CONTENT) {
            height = component.getHeight();
        }
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
        mImageContainer.setSize(width, height);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        reSizeAsImageSize();
    }

    /**
     * 根据宽高属性的wrap或确定的值，重置图片大小
     * 该方法一般在开始整体布局大小确定前调用
     */
    private void reSizeAsImageSize() {
        if (imageIcon == null) {
            width = height = 0;
        } else {
            width = imageIcon.getIconWidth();
            height = imageIcon.getIconHeight();
        }
        if (attribute.getWidth() == BaseAttribute.WRAP_CONTENT) {
            setMinWidth(width);
        }
        if (attribute.getHeight() == BaseAttribute.WRAP_CONTENT) {
            setMinHeight(height);
        }
        mImageContainer.setSize(width, height);
        // 具体的位置只有在绘制的时候才能确定
//        mImageContainer.setLocation((component.getWidth() - width) / 2, (component.getHeight() - height) / 2);
    }

}
