package top.totoro.swing.widget.manager;

import top.totoro.swing.widget.context.Fragment;
import top.totoro.swing.widget.layout.FrameLayout;
import top.totoro.swing.widget.util.Log;

public class FragmentManager {
    public FragmentManager() {
        // 一个Activity全局只能拥有一个实例，因为fragment的切换是原子性的事物
    }

    private FrameLayout mFrameLayout;
    private Fragment mFragment;
    private Boolean isCommitting = false;

    /**
     * 添加要执行fragment的切换事务。
     *
     * @param frameLayout 执行切换的布局节点id
     * @param fragment    要切换到的fragment
     */
    public void transaction(FrameLayout frameLayout, Fragment fragment) {
        // 确保在fragment切换过程中不会有其它切换的事务加入
        // 如果不同步处理的话有可能出现布局加载中断（被其它布局抢占）等风险
        if (isCommitting) return;
        frameLayout.addFragment(fragment);
        this.mFrameLayout = frameLayout;
        this.mFragment = fragment;
    }

    /**
     * 正式执行一个fragment的切换事务
     */
    public void commit() {
        isCommitting = true;
        if (mFrameLayout == null || mFragment == null) {
            Log.d(this, "提交的fragment切换事务无法执行：" +
                    "frameLayout = " + (mFrameLayout == null ? "null" : mFrameLayout) +
                    ", fragment = " + (mFragment == null ? "null" : mFragment))
            ;
            return;
        }
        if (mFrameLayout.checkFragment(mFragment)) {
            // 1) 动态创建fragment的布局
            // 将该layout节点的属性复制给fragment的父视图，决定了fragment的显示
            mFragment.getMainView().setAttribute(mFrameLayout.getAttribute());
            mFragment.getMainView().getComponent().setSize(mFrameLayout.getComponent().getSize());
            // 将自定义的布局添加到fragment中
            mFragment.getMainView().addChildView(mFragment.onCreateView(mFragment.getMainView()));

            // 2) 清除frame layout原本fragment的布局，添加新的fragment的布局
            // 清空Layout的子View，防止View的积累
            mFrameLayout.removeAllSon();
            /*
            remove by HLM on 2020/9/25 功能已经在View.removeAllSon()中实现
            // 清空对应component组件中的组件，确保切换成功
            mFrameLayout.getComponent().removeAll();
            */
            // 添加fragment的布局到该frame layout节点中
            // 实现局部布局切换的基础
            mFrameLayout.addChildView(mFragment.getMainView());

            // 3) 加载fragment布局
            // 需要对fragment进行加载，以确定大小和位置
            mFragment.getLayoutManager().invalidate();
        }
        isCommitting = false;
    }
}
