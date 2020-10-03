package top.totoro.swing.widget.view;

import java.util.Objects;

import static top.totoro.swing.widget.util.AttributeDefaultValue.scaleFitCenter;

/**
 * 图片按钮，图片只能居中缩放显示
 */
public class ImageButton extends ImageView {

    private final String TAG = getClass().getSimpleName();

    public ImageButton(View parent) {
        super(parent);
    }

    @Override
    public void setScaleType(String scaleType, boolean invalidateParent) {
        if (!Objects.equals(this.scaleType, scaleFitCenter)) {
            /* ImageButton的图像必须保持fitCenter */
            this.scaleType = scaleFitCenter;
        }
        // 触发图片的刷新，并决定是否刷新父布局
        setImage(getAttribute().getSrc(), invalidateParent);
    }

}
