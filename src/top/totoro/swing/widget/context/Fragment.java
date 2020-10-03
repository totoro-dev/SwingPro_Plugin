package top.totoro.swing.widget.context;

import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.view.Span;
import top.totoro.swing.widget.view.View;

public class Fragment extends Context {

    /**
     * 加载fragment时，需要动态创建布局，
     *
     * @param parent 当前fragment的布局的父布局，
     *               其实就是context的mainView。
     *               因为动态加载需要从父布局开始加载，
     *               所以fragment的布局创建必须建立在parent之上，
     *               否则无法正常显示。
     * @return 动态创建的布局
     */
    public View<?, ?> onCreateView(BaseLayout parent) {
        return new Span(parent);
    }

    @Override
    public String toString() {
        return "Fragment{}";
    }
}
