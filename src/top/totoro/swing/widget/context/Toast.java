package top.totoro.swing.widget.context;

import top.totoro.swing.widget.layout.ToastContent;

import java.awt.*;
import java.net.URL;

import static java.lang.Thread.sleep;

/**
 * 短暂性提示框
 */
public class Toast extends Dialog {

    private Context context;
    private long showTime = SHORT;
    private ToastContent content;

    public static final long SHORT = 1500;
    public static final long LONG = 3000;
    private static long waitingDelay = 0;

    public Toast(Context context, boolean showMarkWindow) {
        super(context, showMarkWindow);
    }

    /**
     * 构建一个提示框
     *
     * @param context        提示框的上下文
     * @param textOrLayoutId 文本内容或布局id（文件名）
     *                       如果传入的内容能够指向一个布局文件则显示布局
     *                       否则显示为文本内容
     * @return 可以显示的对话框
     */
    public static top.totoro.swing.widget.context.Toast makeText(Context context, String textOrLayoutId) {
        top.totoro.swing.widget.context.Toast toast = new top.totoro.swing.widget.context.Toast(context, false);
        toast.context = context;
        URL url = context.getClass().getClassLoader().getResource("layout/" + textOrLayoutId);
        if (url == null) {
            initToastAsText(toast, textOrLayoutId);
        } else {
            toast.setContentView(textOrLayoutId);
        }
        toast.mDialogWindow.setSize(toast.width, toast.height);
        toast.mDialogWindow.setAlwaysOnTop(true);
        return toast;
    }

    @Override
    public void resetDialogWindowLocation() {
        if (context instanceof Activity && ((Activity) context).isVisible()) {
            Container container = ((Activity) context).getFrame();
            Point location = container.getLocation();
            mDialogWindow.setLocation(location.x + (container.getWidth() - width) / 2 /* 居中 */
                    , location.y + container.getHeight() - height - 10) /* 窗口的底部 */;
        } else {
            // 没有父窗口，或者父窗口最小化了（无法获取窗口的位置），按dialog的位置设置处理
            super.resetDialogWindowLocation();
        }
    }

    /**
     * 初始化提示框的内容为简单文本
     *
     * @param toast 对话框
     * @param text  文本内容
     */
    private static void initToastAsText(top.totoro.swing.widget.context.Toast toast, String text) {
        toast.content = new ToastContent(toast.getMainView(), text);
        toast.mDialogWindow.add(toast.content.getComponent());
        toast.width = 30;
        toast.height = 30;
        char[] chars = text.toCharArray();
        for (char c :
                chars) {
            if (Integer.parseInt(Integer.toString(c)) < 128) {
                toast.width += 8;
            } else {
                if (String.valueOf(c).matches("。？、“”——")) {
                    toast.width += 10;
                } else {
                    toast.width += 16;
                }
            }
        }
        toast.resetDialogWindowLocation();
    }

    @Override
    public void show() {
        new Thread(() -> {
            try {
                long needToWait = waitingDelay;
                waitingDelay += showTime;
                sleep(needToWait);
                super.show();
                sleep(showTime);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            } finally {
                dismiss();
            }
        }).start();
    }

    /**
     * 在显示指定时间之后隐藏
     *
     * @param delayToDismiss 显示时间
     */
    public void show(long delayToDismiss) {
        this.showTime = delayToDismiss;
        show();
    }

    @Override
    public void dismiss() {
        context = null;
        content = null;
        super.dismiss();
        waitingDelay -= this.showTime;
    }
}
