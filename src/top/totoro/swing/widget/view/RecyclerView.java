package top.totoro.swing.widget.view;

import top.totoro.swing.widget.bar.HorizontalScrollBar;
import top.totoro.swing.widget.bar.VerticalScrollBar;
import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.base.BaseScrollBar;
import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.layout.LinearLayout;
import top.totoro.swing.widget.listener.InvalidateListener;
import top.totoro.swing.widget.manager.LayoutManager;
import top.totoro.swing.widget.util.Log;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * 列表
 */
@SuppressWarnings("Duplicates")
public class RecyclerView extends LinearLayout implements InvalidateListener {

    private LayoutManager layoutManager = new LayoutManager();

    public static final int HORIZONTAL = 1, VERTICAL = 2;
    //    private JPanel parent; // 放置这个RecyclerView的容器
    //    private JPanel component; // 在这个RecyclerView中放置子控件的容器
    private BaseLayout container = new BaseLayout(null);
    private LayoutAttribute containerAttribute = new LayoutAttribute();
    private BaseScrollBar.Vertical verticalScrollBar; // 垂直滚动条
    private BaseScrollBar.Horizontal horizontalScrollBar; // 水平滚动条
    private int orientation; // 这个RecyclerView的布局方向
    private Adapter adapter; // 这个RecyclerView持有的适配器
    /* 适配器对应的所有RecyclerView实例，用于在适配器的数据集发生改变时进行通知这些RecyclerView实例
     * 使用Weak的弱引用模式，确保在适配器不再有RecyclerView持有时，从映射关系中自动删除并GC*/
    private static final WeakHashMap<Adapter, List<top.totoro.swing.widget.view.RecyclerView>> instances = new WeakHashMap<>();

    private boolean mousePressing; // 鼠标是否按住
    private boolean shiftPressing; // Shift键是否按住

    private int width, height; // container的宽高

    private int verticalBarHeight = 0;
    private int verticalBarY = 0, clickY = 0;
    private int maxVerticalBarY = 0;

    private int horizontalBarWidth = 0;
    private int horizontalBarX = 0, clickX = 0;
    private int maxHorizontalBarX = 0;

    public RecyclerView(View parent) {
        super(parent);
        verticalScrollBar = new VerticalScrollBar();
        horizontalScrollBar = new HorizontalScrollBar();
        containerAttribute.setWidth(0);
        containerAttribute.setHeight(0);
        container.setAttribute(containerAttribute);
        container.setLayoutManager(layoutManager);
        initMainListener();
        setOrientation(VERTICAL);
        component.setFocusable(true); // 使得RecyclerView可以监听键盘事件
        if (context != null) {
            context.requestInvalidateListener(this);
        }
    }

    @Override
    public void onInvalidateFinished() {
        reSizeScrollBar();
    }


    public static abstract class Adapter<ViewHolder extends top.totoro.swing.widget.view.RecyclerView.ViewHolder> {

        public abstract ViewHolder onCreateViewHolder(BaseLayout parent);

        public abstract void onBindViewHolder(ViewHolder holder, int position, int viewType);

        public abstract int getItemCount();

        /**
         * 通知持有该适配器的所有RecyclerView，数据集发生改变
         * 修改这些RecyclerView的内容
         * 如果正在浏览位置前的数据集数量变少，会导致浏览的位置后移。
         * 如果正在浏览位置前的数据集数量变多，会导致浏览的位置前移。
         * 如果正在浏览位置前的数据集数量不变，不改变正在浏览的位置。
         */
        public void notifyDataSetChange() {
            List<top.totoro.swing.widget.view.RecyclerView> list = instances.get(this);
            if (list == null || list.size() == 0) return;
            for (top.totoro.swing.widget.view.RecyclerView instance : list) {
                if (instance == null) continue;
                instance.setAdapter(this);
            }
        }

        public int getViewType(int position) {
            return 0;
        }
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        List<top.totoro.swing.widget.view.RecyclerView> list = instances.get(adapter);
        if (list == null) {
            list = new ArrayList<>();
            list.add(this);
            instances.put(adapter, list);
        } else if (!list.contains(this)) {
            list.add(this);
        }
        if (orientation == VERTICAL) {
            setVerticalAdapter();
        } else if (orientation == HORIZONTAL) {
            setHorizontalAdapter();
        }
    }

    /**
     * 设置垂直滚动的RecyclerView对应的子控件的适配器
     */
    private void setVerticalAdapter() {
        container.removeAllSon();
        container.getComponent().removeAll();
        containerAttribute.setWidth(component.getWidth());
        containerAttribute.setHeight(component.getHeight());
        container.getComponent().setLocation(0, 0);
        container.getComponent().setSize(component.getSize());
        component.removeAll();
        component.add(verticalScrollBar);
        int count = adapter.getItemCount();
        width = 0;
        height = 0;
        for (int i = 0; i < count; i++) {
            ViewHolder item = adapter.onCreateViewHolder(null);
            /* remove by HLM on 2020/7/26 解决ViewHolder的刷新影响全局刷新的问题 */
//            item.getView().setContext(context);
            /* remove end */
            /* add by HLM on 2020/7/26 解决鼠标等事件被ViewHolder中的view拦截问题 */
            // change by HLM on 2020/10/2 简化事件冒泡
            item.getView().setParent(this);
            /* add end */
            adapter.onBindViewHolder(item, i, adapter.getViewType(i));
            container.addChildView(item.getView());
            layoutManager.invalidate((BaseLayout) item.getView());
            height += item.getView().getComponent().getHeight();
            if (width < item.getView().getComponent().getWidth()) width = item.getView().getComponent().getWidth();
        }
        // 不使用addChildView，是为了不将container与RecyclerView捆绑，从而发生id冲突
        // 并且这样就不会在全局刷新时影响到container的布局
        component.add(container.getComponent());
        /* add by HLM on 2020/04/23 解决无法显示的问题 */
        containerAttribute.setHeight(height);
        /* add end */
        context.invalidate();
    }

    /**
     * 设置横向滚动的RecyclerView对应的子控件的适配器
     */
    private void setHorizontalAdapter() {
        container.getComponent().removeAll();
        containerAttribute.setWidth(component.getWidth());
        containerAttribute.setHeight(component.getHeight());
        container.getComponent().setLocation(0, 0);
        container.getComponent().setSize(component.getSize());
        component.removeAll();
        component.add(horizontalScrollBar);
        int count = adapter.getItemCount();
        width = 0;
        height = 0;
        for (int i = 0; i < count; i++) {
            ViewHolder item = adapter.onCreateViewHolder(null);
//            item.getView().setLayoutManager(layoutManager);
            /* add by HLM on 2020/7/26 解决鼠标等事件被ViewHolder中的view拦截问题 */
            // change by HLM on 2020/10/2 简化事件冒泡
            item.getView().setParent(this);
            /* add end */
            adapter.onBindViewHolder(item, i, adapter.getViewType(i));
            container.addChildView(item.getView());
            layoutManager.invalidate((BaseLayout) item.getView());
            width += item.getView().getComponent().getWidth();
            if (height < item.getView().getComponent().getHeight()) height = item.getView().getComponent().getHeight();
        }
        component.add(container.getComponent());
        /* add by HLM on 2020/04/23 解决无法显示的问题 */
        containerAttribute.setWidth(width);
        /* add end */
        context.invalidate();
    }

    /**
     * 垂直方向滚动布局
     *
     * @param gap 滚轮滚动长度
     */
    private boolean scrolledVertical(int gap) {
        int tmp = gap + verticalBarY;
        if (tmp > maxVerticalBarY) {
            // 垂直方向上，设置滚动至底部
            container.getComponent().setLocation(container.getComponent().getX(), component.getHeight() - container.getComponent().getHeight());
        } else if (tmp < 0) {
            // 垂直方向上，设置滚动至顶部
            container.getComponent().setLocation(container.getComponent().getX(), 0);
        } else if (gap >= 1 || gap <= -1) {
            if (component.getHeight() == verticalBarHeight) return true;
            // 滚动比例，带符号。"+"：垂直向下滚动；"-"：垂直向上滚动
            // getHeight() - verticalBarHeight可滚动的长度，相当于容器的不可见长度
            double scale = gap / (double) (component.getHeight() - verticalBarHeight);
            int scrollHeight = (int) ((container.getComponent().getHeight() - component.getHeight()) * scale);
            if (container.getComponent().getY() + container.getComponent().getHeight() - component.getHeight() < 0 && scrollHeight <= 0)
                return true;
            if (container.getComponent().getY() + component.getHeight() - container.getComponent().getHeight() > 0 && scrollHeight >= 0)
                return true;
            verticalBarY = tmp;
            verticalScrollBar.setBarY(verticalBarY);
            container.getComponent().setLocation(container.getComponent().getX(), container.getComponent().getY() - scrollHeight);
            return true;
        }
        return false;
    }

    /**
     * 水平方向滚动布局
     *
     * @param gap 滚轮滚动长度
     */
    private boolean scrolledHorizontal(int gap) {
        int tmp = gap + horizontalBarX;
        if (tmp > maxHorizontalBarX) {
            //水平方向上，设置滚动至右端
            container.getComponent().setLocation(component.getWidth() - container.getComponent().getWidth(), container.getComponent().getY());
        } else if (tmp < 0) {
            // 水平方向上，设置滚动至左端
            container.getComponent().setLocation(0, container.getComponent().getY());
        } else if (gap >= 1 || gap <= -1) {
            if (component.getWidth() == horizontalBarWidth) return true;
            // getWidth() - horizontalBarWidth可滚动的宽度，相当于容器的不可见宽度
            // 滚动比例，带符号。"+"：水平向右滚动；"-"：水平向左滚动
            double scale = gap / (double) (component.getWidth() - horizontalBarWidth);
            int scrollWidth = (int) ((container.getComponent().getWidth() - component.getWidth()) * scale);
            if (container.getComponent().getX() + container.getComponent().getWidth() - component.getWidth() < 0 && scrollWidth <= 0)
                return true;
            if (container.getComponent().getX() + component.getWidth() - container.getComponent().getWidth() > 0 && scrollWidth >= 0)
                return true;
            horizontalBarX = tmp;
            horizontalScrollBar.setBarX(horizontalBarX);
            container.getComponent().setLocation(container.getComponent().getX() - scrollWidth, container.getComponent().getY());
            return true;
        }
        return false;
    }

    /**
     * 当布局刷新之后需要由LayoutManager来刷新滚动条
     * 因为只有全局刷新完成才会由确定的宽高，进而准确的设置
     */
    public void reSizeScrollBar() {
        if (container.getComponent().getWidth() <= 0) return;
        if (container.getComponent().getHeight() <= 0) return;
        if (component.getWidth() >= container.getComponent().getWidth()) {
            container.getComponent().setLocation(0, container.getComponent().getY());
        }
        if (component.getHeight() >= container.getComponent().getHeight()) {
            container.getComponent().setLocation(container.getComponent().getX(), 0);
        }
        if (orientation == VERTICAL) {
            containerAttribute.setWidth(component.getWidth());
            containerAttribute.setHeight(height);
            layoutManager.invalidate(container);
            // 垂直方向上，滚轮滚动长度与容器高度的比例
            double vertical = component.getHeight() / (double) height;
            verticalBarHeight = (int) (component.getHeight() * vertical);
            maxVerticalBarY = component.getHeight() - verticalBarHeight;
            verticalScrollBar.setBarHeight(verticalBarHeight);
            verticalBarY = -container.getComponent().getY() * maxVerticalBarY / container.getComponent().getHeight();
            verticalScrollBar.setBarY(verticalBarY);
            verticalScrollBar.setBounds(component.getWidth() - verticalScrollBar.getWidth(), 0, verticalScrollBar.getWidth(), component.getHeight());
        } else if (orientation == HORIZONTAL) {
            containerAttribute.setWidth(width);
            containerAttribute.setHeight(component.getHeight());
            layoutManager.invalidate(container);
            // 垂直方向上，滚轮滚动长度与容器高度的比例
            double horizontal = component.getWidth() / (double) width;
            horizontalBarWidth = (int) (component.getWidth() * horizontal);
            maxHorizontalBarX = component.getWidth() - horizontalBarWidth;
            horizontalScrollBar.setBarWidth(horizontalBarWidth);
            horizontalBarX = -container.getComponent().getX() * maxHorizontalBarX / container.getComponent().getWidth();
            horizontalScrollBar.setBarX(horizontalBarX);
            horizontalScrollBar.setBounds(0, component.getHeight() - horizontalScrollBar.getHeight(), component.getWidth(), horizontalScrollBar.getHeight());
        }
    }

    public static class ViewHolder {
        private View item;

        public ViewHolder(View item) {
            this.item = item;
        }

        public View getView() {
            return item;
        }
    }

    /**
     * 设置布局的滚动分向
     *
     * @param orientation 布局方向，对应：HORIZONTAL = 1, VERTICAL = 2;
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        if (orientation == HORIZONTAL) {
            containerAttribute.setOrientation(LayoutAttribute.HORIZONTAL);
            initHorizontalListener();
        } else if (orientation == VERTICAL) {
            containerAttribute.setOrientation(LayoutAttribute.VERTICAL);
            initVerticalListener();
        }
        /* 如果当前的RecyclerView已经持有适配器，需要重置布局方向*/
        if (adapter != null) setAdapter(adapter);
    }

    /**
     * 设置自定义的垂直滚动条
     *
     * @param verticalScrollBar 自定义的滚动条
     */
    public void setVerticalScrollBar(BaseScrollBar.Vertical verticalScrollBar) {
        this.verticalScrollBar = verticalScrollBar;
        initVerticalListener();
    }

    /**
     * 设置自定义的水平滚动条
     *
     * @param horizontalScrollBar 自定义的滚动条
     */
    public void setHorizontalScrollBar(BaseScrollBar.Horizontal horizontalScrollBar) {
        this.horizontalScrollBar = horizontalScrollBar;
        initHorizontalListener();
    }

    private void initMainListener() {
        component.addMouseWheelListener(e -> {
            if (component.getHeight() >= container.getComponent().getHeight() && orientation == VERTICAL) {
                Log.d("mouseWheelMoved", "is vertical but not scroll");
                return;
            }
            if (component.getWidth() >= container.getComponent().getWidth() && orientation == HORIZONTAL) {
                Log.d("mouseWheelMoved", "is horizontal but not scroll");
                return;
            }
            int gap = e.getWheelRotation() * e.getScrollAmount();
            if (verticalScrollBar.getVisible()) {
                scrolledVertical(gap);
            }
            if (horizontalScrollBar.getVisible() && shiftPressing) {
                scrolledHorizontal(gap);
            }
        });
        component.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mousePressing = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressing = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (component.getHeight() >= container.getComponent().getHeight() && orientation == VERTICAL) {
                    Log.d("mouseEntered", "is vertical but not visible");
                    return;
                }
                if (component.getWidth() >= container.getComponent().getWidth() && orientation == HORIZONTAL) {
                    Log.d("mouseEntered", "is horizontal but not visible");
                    return;
                }
                if (orientation == VERTICAL) {
                    verticalScrollBar.setVisible(true);
                    verticalScrollBar.repaint();
                }
                if (orientation == HORIZONTAL) {
                    horizontalScrollBar.setVisible(true);
                    horizontalScrollBar.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (mousePressing) return;
                verticalScrollBar.setVisible(false);
                horizontalScrollBar.setVisible(false);
            }
        });
        component.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressing = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftPressing = false;
                }
            }
        });
    }

    private MouseListener verticalMouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            clickY = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (component.getHeight() >= container.getComponent().getHeight() && orientation == VERTICAL) {
                Log.d("mouseEntered", "is vertical but not visible");
                return;
            }
            if (component.getWidth() >= container.getComponent().getWidth() && orientation == HORIZONTAL) {
                Log.d("mouseEntered", "is horizontal but not visible");
                return;
            }
            verticalScrollBar.setVisible(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (mousePressing) return;
            verticalScrollBar.setVisible(false);
        }
    };
    private MouseMotionListener verticalMouseMotionLister = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e) {
            int gap = e.getY() - clickY;
            if (scrolledVertical(gap)) {
                clickY = e.getY();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    };
    private MouseListener horizontalMouseListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            clickX = e.getX();
            mousePressing = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mousePressing = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (component.getHeight() >= container.getComponent().getHeight() && orientation == VERTICAL) {
                Log.d("mouseEntered", "is vertical but not visible");
                return;
            }
            if (component.getWidth() >= container.getComponent().getWidth() && orientation == HORIZONTAL) {
                Log.d("mouseEntered", "is horizontal but not visible");
                return;
            }
            horizontalScrollBar.setVisible(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (mousePressing) return;
            horizontalScrollBar.setVisible(false);
        }
    };
    private MouseMotionListener horizontalMouseMotionLister = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e) {
            int gap = e.getX() - clickX;
            if (scrolledHorizontal(gap)) {
                clickX = e.getX();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    };

    private void initVerticalListener() {
        verticalScrollBar.setMouseListener(verticalMouseListener);
        verticalScrollBar.setMouseMotionListener(verticalMouseMotionLister);
    }

    private void initHorizontalListener() {
        horizontalScrollBar.setMouseListener(horizontalMouseListener);
        horizontalScrollBar.setMouseMotionListener(horizontalMouseMotionLister);
    }

    /* 解决被ViewHolder拦截，无法监听到鼠标事件的问题 */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        verticalMouseListener.mouseEntered(e);
        horizontalMouseListener.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        verticalMouseListener.mouseExited(e);
        horizontalMouseListener.mouseExited(e);
    }
}
