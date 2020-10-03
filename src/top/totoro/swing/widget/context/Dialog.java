package top.totoro.swing.widget.context;

import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.manager.DialogManager;
import top.totoro.swing.widget.util.SwingConstants;
import top.totoro.swing.widget.view.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 对话框
 */
public class Dialog extends Context {
    private final long mDialogId = System.currentTimeMillis();
    private final JWindow mDialogMarkWindow; // 蒙版
    protected final JWindow mDialogWindow;
    private final Context mContext;
    private int mContextWidth = 0, mContextHeight = 0;
    protected int width = 0, height = 0;
    private boolean mShowing = false; // 是否处于显示状态（不受窗口最小化的影响）

    public Dialog(Context context, boolean showMarkWindow) {
        assert context != null;
        this.mContext = context;
        if (mContext instanceof Activity) {
            mContextWidth = context.getSize().width;
            mContextHeight = context.getSize().height;
        } else {
            Dimension screenSize = SwingConstants.getScreenSize();
            mContextWidth = screenSize.width;
            mContextHeight = screenSize.height;
        }

        if (showMarkWindow) {
            width = mContextWidth - 50;
            height = mContextHeight - 50;
        } else {
            width = mContextWidth;
            height = mContextHeight;
        }

        mDialogMarkWindow = new JWindow();
        if (showMarkWindow) {
            mDialogMarkWindow.setSize(mContextWidth, mContextHeight);
            mDialogMarkWindow.setOpacity(0.5F);
            MouseListener mDialogMarkWindowMouseListener = new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dismiss();
                }

                public void mousePressed(MouseEvent e) {

                }

                public void mouseReleased(MouseEvent e) {

                }

                public void mouseEntered(MouseEvent e) {

                }

                public void mouseExited(MouseEvent e) {

                }
            };
            mDialogMarkWindow.addMouseListener(mDialogMarkWindowMouseListener);
        }
        mDialogWindow = new JWindow();
        mDialogWindow.getContentPane().add(getMainView().getComponent());
        // 设置背景全透明
        mDialogWindow.getRootPane().setOpaque(false);
        mDialogWindow.setSize(mContextWidth, mContextHeight);
    }

    public Dialog(Context context) {
        this(context, true);
    }

    /**
     * 显示这个对话框
     */
    public void show() {
        // 保证永远只有一个dialog显示中
        if (!(this instanceof Toast) /* 显示Toast时不需要将其它类型的dialog隐藏 */
                && DialogManager.getTopDialog() != null
                && DialogManager.getTopDialog().mDialogId != mDialogId) {
            DialogManager.dismiss();
        }
        if (mDialogMarkWindow != null && mDialogWindow != null) {
            mDialogMarkWindow.setVisible(true);
            mDialogWindow.setVisible(true);
            DialogManager.setTopDialog(this);
            mShowing = true;
        }
    }

    /**
     * 隐藏这个对话框。
     * 有可能是由于界面最小化导致的隐藏，所以需要指定是否需要重新显示对话框，默认不显示
     *
     * @param needToShowingAuto 是否需要在界面重新可见的时候自动显示对话框
     */
    public void hide(boolean... needToShowingAuto) {
        if (mDialogMarkWindow != null && mDialogWindow != null) {
            mDialogMarkWindow.setVisible(false);
            mDialogWindow.setVisible(false);
            mShowing = needToShowingAuto.length > 0 && needToShowingAuto[0];
        }
    }

    /**
     * 销毁这个对话框
     */
    public void dismiss() {
        if (mDialogMarkWindow != null && mDialogWindow != null) {
            hide();
            mDialogMarkWindow.dispose();
            mDialogWindow.dispose();
            DialogManager.setTopDialog(null);
        }
    }

    /**
     * 是否处于可见状态，
     * 可能由于界面最小化而导致的暂时隐藏，但也属于可见状态
     *
     * @return 是否可见
     */
    public boolean isShowing() {
        return mDialogMarkWindow != null && mDialogWindow != null && mShowing;
    }

    /**
     * 给对话框指定一个具体的布局
     *
     * @param layoutId 布局的id（文件名）
     */
    public void setContentView(String layoutId) {
        getMainView().removeAllSon();
        // 将activity的窗口大小赋予dialog的大小
        LayoutAttribute mainViewAttr = new LayoutAttribute();
        mainViewAttr.setId("dialog_main_view");
        mainViewAttr.setWidth(width);
        mainViewAttr.setHeight(height);
        getMainView().setAttribute(mainViewAttr);

        layoutManager.inflate(getMainView(), layoutId);
        layoutManager.setMainLayout(getMainView());
        View<?, ?> contentView = getMainView().getSonByIndex(0);
        if (contentView.getAttribute().getWidth() >= 0) {
            width = contentView.getComponent().getWidth();
        }
        if (contentView.getAttribute().getHeight() >= 0) {
            height = contentView.getComponent().getHeight();
        }
        mainViewAttr.setWidth(width);
        mainViewAttr.setHeight(height);
        mDialogWindow.setSize(width, height);
        resetDialogWindowLocation();
        layoutManager.invalidate();
    }

    /**
     * 刷新对话框的位置，确保和父窗口一致
     */
    public void resetDialogWindowLocation() {
        int parentX = 0, parentY = 0;
        if (mContext instanceof Activity) {
            if (((Activity) mContext).isVisible()) {
                // 只有是activity窗口并且可见时才需要定位窗口的位置
                parentX = ((Activity) mContext).getFrame().getX();
                parentY = ((Activity) mContext).getFrame().getY();
            } else {
                Dimension screenSize = SwingConstants.getScreenSize();
                // 窗口不可见，使dialog相对屏幕居中显示
                parentX = (screenSize.width - mContextWidth) / 2;
                parentY = (screenSize.height - mContextHeight) / 2;
            }
        }
        int x = parentX + (mContextWidth - width) / 2;
        int y = parentY + (mContextHeight - height) / 2;
        mDialogMarkWindow.setLocation(parentX, parentY);
        mDialogWindow.setLocation(x, y);
    }

}
