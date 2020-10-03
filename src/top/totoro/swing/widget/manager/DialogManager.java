package top.totoro.swing.widget.manager;

import top.totoro.swing.widget.context.Dialog;

public class DialogManager {
    private static Dialog mTopDialog;

    public static Dialog getTopDialog() {
        return mTopDialog;
    }

    public static void setTopDialog(Dialog mTopDialog) {
        // Toast虽然属于dialog，但是它一段时间后会自动消失，所以不需要进行管理
//        if (mTopDialog instanceof Toast) return;
        top.totoro.swing.widget.manager.DialogManager.mTopDialog = mTopDialog;
    }

    public static void dismiss() {
        if (mTopDialog != null) {
            mTopDialog.dismiss();
            mTopDialog = null;
        }
    }

}
