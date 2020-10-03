package top.totoro.swing.widget.layout;

import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.manager.LayoutManager;
import top.totoro.swing.widget.view.View;

public class LayoutInflater {
    private static LayoutManager manager = new LayoutManager();

    public static View inflate(BaseLayout mainLayout, String res, boolean attachRoot) {
        View view = manager.inflate(mainLayout, res, attachRoot);
        manager.invalidate();
        return view;
    }
}
