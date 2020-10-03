package top.totoro.swing.widget.manager;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import top.totoro.swing.widget.base.BaseAttribute;
import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.bean.LayoutAttribute;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.exception.AttributeException;
import top.totoro.swing.widget.layout.CenterLayout;
import top.totoro.swing.widget.layout.FrameLayout;
import top.totoro.swing.widget.layout.GridLayout;
import top.totoro.swing.widget.layout.LinearLayout;
import top.totoro.swing.widget.util.*;
import top.totoro.swing.widget.view.Span;
import top.totoro.swing.widget.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * 视图的管理
 * 1、根据资源文件加载视图（xml解析）
 * 2、渲染视图
 * 3、刷新视图
 */
@SuppressWarnings("ALL")
public class LayoutManager {

    private static final String LAYOUT_RESOURCE_PATH = "layout/";
    private BaseLayout mainLayout;
    private BaseLayout mainView;
    private LinkedList<View> sonViews;
    private int itemWidth = 0, itemHeight = 0;

    public LayoutManager() {
    }

    public void setMainLayout(BaseLayout mainLayout) {
        this.mainLayout = mainLayout;
    }

    /**
     * 通过xml资源文件名，初始化View的关系列表，为渲染做准备
     * 默认绑定到mainLayout上
     *
     * @param mainLayout 这个xml解析后的View要绑定的Layout节点
     * @param res        xml文件名，这个文件要放在：（项目路径）/layout下
     */
    public View inflate(BaseLayout mainLayout, String res) {
        if (mainLayout.getComponent() != null) {
            mainLayout.getComponent().removeAll();
        }
        URL url = mainLayout.getClass().getClassLoader().getResource(LAYOUT_RESOURCE_PATH + res);
        try {
            if (url == null) throw new FileNotFoundException("未找到'" + res + "'资源文件");
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(url);
            Element root = document.getRootElement();
            LayoutAttribute layoutAttribute = AttributeUtil.getLayoutAttribute(res, root, true);
            mainView = LayoutUtil.createLayout(mainLayout, root.getName(), layoutAttribute);
            if (mainView == null) throw new AttributeException(res + "资源文件的根节点必须继承BaseLayout");
            if (layoutAttribute.getId() != null) {
                mainView.setId(layoutAttribute.getId());
            }
            /* change by HLM on 2020/7/27 增加Centerlayoutd的加载逻辑 */
            addChildView(mainLayout, mainView, mainLayout.getAttribute());
            /* change end */
            attachLayout(mainView, root, res, true);
        } catch (AttributeException | JDOMException | IOException e) {
            e.printStackTrace();
        }
        this.mainLayout = mainLayout;
        return mainLayout;
    }

    /**
     * 通过xml资源文件名，初始化View的关系列表，为渲染做准备
     * 默认绑定到mainLayout上
     *
     * @param mainLayout 这个xml解析后的View要绑定的Layout节点
     * @param res        xml文件名，这个文件要放在：（项目路径）/layout下
     */
    public View inflateByAbsolutePath(BaseLayout mainLayout, String resAbsolutePath) {
        if (mainLayout.getComponent() != null) {
            mainLayout.getComponent().removeAll();
        }
//        URL url = mainLayout.getClass().getClassLoader().getResource(LAYOUT_RESOURCE_PATH + res);
        try {
            if (resAbsolutePath == null) throw new FileNotFoundException("资源文件的绝对路径为空");
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(new File(resAbsolutePath));
            Element root = document.getRootElement();
            LayoutAttribute layoutAttribute = AttributeUtil.getLayoutAttribute(resAbsolutePath, root, true);
            mainView = LayoutUtil.createLayout(mainLayout, root.getName(), layoutAttribute);
            if (mainView == null) throw new AttributeException(resAbsolutePath + "资源文件的根节点必须继承BaseLayout");
            if (layoutAttribute.getId() != null) {
                mainView.setId(layoutAttribute.getId());
            }
            /* change by HLM on 2020/7/27 增加Centerlayoutd的加载逻辑 */
            addChildView(mainLayout, mainView, mainLayout.getAttribute());
            /* change end */
            attachLayout(mainView, root, resAbsolutePath, true);
        } catch (JDOMException | AttributeException | IOException e) {
            e.printStackTrace();
        }
        this.mainLayout = mainLayout;
        return mainLayout;
    }


    /**
     * 通过xml资源文件名，初始化View的关系列表，为渲染做准备
     *
     * @param mainLayout 这个xml解析后的View要绑定的Layout节点
     * @param res        xml文件名，这个文件要放在：（项目路径）/layout下
     * @param attachRoot 是否将解析后的View绑定到mainLayout的子ID列表中，不绑定的话，就不会发生id冲突，绑定的话id就要保持一个全局视图中是唯一的
     */
    public View inflate(BaseLayout mainLayout, String res, boolean attachRoot) {
        Log.d("inflate", res);
        if (mainLayout != null && mainLayout.getComponent() != null) {
            mainLayout.getComponent().removeAll();
        }
        URL url = getClass().getClassLoader().getResource(LAYOUT_RESOURCE_PATH + res);
        try {
            if (url == null) throw new FileNotFoundException("未找到'" + res + "'资源文件");
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(url);
            Element root = document.getRootElement();
            LayoutAttribute layoutAttribute = AttributeUtil.getLayoutAttribute(res, root, true);
            mainView = LayoutUtil.createLayout(mainLayout, root.getName(), layoutAttribute);
            if (mainView == null) throw new AttributeException(res + "资源文件的根节点必须继承BaseLayout");
            /* add by HLM on 2020/7/26 解决子布局加载时发生id冲突 */
            if (attachRoot && layoutAttribute.getId() != null) {
                mainView.setId(layoutAttribute.getId());
            }
            attachLayout(mainView, root, res, attachRoot);
            if (mainLayout != null && attachRoot) {
                /* change by HLM on 2020/7/27 增加Centerlayoutd的加载逻辑 */
                addChildView(mainLayout, mainView, mainLayout.getAttribute());
                /* change end */
                this.mainLayout = mainLayout;
            }
        } catch (JDOMException | AttributeException | IOException e) {
            e.printStackTrace();
        }
        return mainLayout != null && attachRoot ? mainLayout : mainView;
    }

    /**
     * 向layout布局中添加子控件，需要判断当前layout的类型，并作相应的调整。
     * 如果layout是CenterLayout，需要在要添加的控件的前后各添加一个Span来达到居中的效果。
     *
     * @param layout          父布局
     * @param son             子控件，如果是要居中，则需要有明确的大小
     * @param layoutAttribute 父布局的布局属性
     */
    private void addChildView(BaseLayout layout, View son, LayoutAttribute layoutAttribute) {
        if (layout instanceof CenterLayout || layoutAttribute.getGravity() == AttributeDefaultValue.center) {
            Span left = new Span(layout);
            ViewAttribute leftAttr = new ViewAttribute();
            // 根据layout的Orientation属性确定是上下居中还是左右居中
            if (layoutAttribute.getOrientation() == LayoutAttribute.VERTICAL) {
                leftAttr.setHeight(BaseAttribute.MATCH_PARENT);
                leftAttr.setWidth(0);
            } else {
                leftAttr.setWidth(BaseAttribute.MATCH_PARENT);
                leftAttr.setHeight(0);
            }
            left.setAttribute(leftAttr);
            layout.addChildView(left);
        }

        layout.addChildView(son);

        if (layout instanceof CenterLayout) {
            Span right = new Span(layout);
            ViewAttribute rightAttr = new ViewAttribute();
            if (layoutAttribute.getOrientation() == LayoutAttribute.VERTICAL) {
                rightAttr.setHeight(BaseAttribute.MATCH_PARENT);
                rightAttr.setWidth(0);
            } else {
                rightAttr.setWidth(BaseAttribute.MATCH_PARENT);
                rightAttr.setHeight(0);
            }
            right.setAttribute(rightAttr);
            layout.addChildView(right);
        }
    }

    private void attachLayout(BaseLayout root, Element rootElement, String res, boolean atachRoot) {
        if (root.getComponent() != null) {
            root.getComponent().removeAll();
        }
        // 增加gravity属性，需要对center属性做居中处理
        // 因为CenterLayout继承自LinearLayout，需要先判断CenterLayout，否则会被匹配为LinearLayout
        if (root instanceof CenterLayout || root.getAttribute().getGravity().equals(AttributeDefaultValue.center)) { // 解析居中布局
            attachAsCenterLayout(root, rootElement, res, atachRoot);
        } else if (root instanceof LinearLayout) {    // 解析线性布局
            attachAsLinearLayout(root, rootElement, res, atachRoot);
        } else if (root instanceof GridLayout) {
            attachAsGridLayout(root, rootElement, res, atachRoot);
        }
    }

    private void attachAsGridLayout(BaseLayout root, Element rootElement, String res, boolean atachRoot) {
        List<Element> childElements = rootElement.getChildren();
        for (Element childElement : childElements) {
            BaseAttribute childAttribute;
            Log.d(this, "element name = " + childElement.getName());
            if (childElement.getChildren().size() > 0 || childElement.getName().equals(FrameLayout.class.getSimpleName())) {
                // 这个子节点是一个Layout
                childAttribute = AttributeUtil.getLayoutAttribute(res, childElement, false);
                BaseLayout layout = LayoutUtil.createLayout(root, childElement.getName(), (LayoutAttribute) childAttribute);
                if (layout != null) {
                    if (childAttribute.getId() != null) {
                        layout.setId(childAttribute.getId());
                    }
                    // 以当前子节点开始绑定View
                    attachLayout(layout, childElement, res, atachRoot);
                    root.addChildView(layout);
                }
            } else {
                // 这个节点是一个子View
                // 也可能是RecyclerView
                childAttribute = AttributeUtil.getViewAttribute(res, childElement, false);
                View view = ViewUtil.createView(root, childElement.getName(), (ViewAttribute) childAttribute);
                if (view instanceof BaseLayout) {
                    // 这个子节点是一个Layout
                    childAttribute = AttributeUtil.getLayoutAttribute(res, childElement, false);
                    view.setAttribute(childAttribute);
                    root.addChildView(view);
                } else {
                    root.addChildView(view);
                }
                if (childAttribute.getId() != null) {
                    view.setId(childAttribute.getId());
                }
            }
        }
    }

    private void attachAsLinearLayout(BaseLayout root, Element rootElement, String res, boolean atachRoot) {
        List<Element> childElements = rootElement.getChildren();
        for (Element childElement : childElements) {
            BaseAttribute childAttribute;
            Log.d(this, "element name = " + childElement.getName());
            if (childElement.getChildren().size() > 0 || childElement.getName().equals(FrameLayout.class.getSimpleName())) {
                // 这个子节点是一个Layout
                childAttribute = AttributeUtil.getLayoutAttribute(res, childElement, true);
                BaseLayout layout = LayoutUtil.createLayout(root, childElement.getName(), (LayoutAttribute) childAttribute);
                if (layout != null) {
                    if (childAttribute.getId() != null) {
                        layout.setId(childAttribute.getId());
                    }
                    // 以当前子节点开始绑定View
                    attachLayout(layout, childElement, res, atachRoot);
                    root.addChildView(layout);
                }
            } else {
                // 这个节点是一个子View
                childAttribute = AttributeUtil.getViewAttribute(res, childElement, true);
                View view = ViewUtil.createView(root, childElement.getName(), (ViewAttribute) childAttribute);
                if (view instanceof BaseLayout) {
                    // 这个子节点是一个Layout
                    childAttribute = AttributeUtil.getLayoutAttribute(res, childElement, true);
                    view.setAttribute(childAttribute);
                    root.addChildView(view);
                } else {
                    root.addChildView(view);
                }
                if (childAttribute.getId() != null) {
                    view.setId(childAttribute.getId());
                }
            }
        }
    }

    private void attachAsCenterLayout(BaseLayout root, Element rootElement, String res, boolean atachRoot) {
        Span left = new Span(root);
        ViewAttribute leftAttr = new ViewAttribute();
        if (root.getAttribute().getOrientation() == LayoutAttribute.VERTICAL) {
            leftAttr.setHeight(BaseAttribute.MATCH_PARENT);
            leftAttr.setWidth(0);
        } else {
            leftAttr.setWidth(BaseAttribute.MATCH_PARENT);
            leftAttr.setHeight(0);
        }
        left.setAttribute(leftAttr);
        root.addChildView(left);

        attachAsLinearLayout(root, rootElement, res, atachRoot);

        Span right = new Span(root);
        ViewAttribute rightAttr = new ViewAttribute();
        if (root.getAttribute().getOrientation() == LayoutAttribute.VERTICAL) {
            rightAttr.setHeight(BaseAttribute.MATCH_PARENT);
            rightAttr.setWidth(0);
        } else {
            rightAttr.setWidth(BaseAttribute.MATCH_PARENT);
            rightAttr.setHeight(0);
        }
        right.setAttribute(rightAttr);
        root.addChildView(right);
    }

    /**
     * 渲染指定的资源文件的视图，这个资源文件必须是在这之前，通过initViewListByRes方法初始化过的。
     */
    public void invalidate() {
        if (mainLayout != null) {
            measureAllViewSizeAsValue(mainLayout);
            measureLayoutSizeAsWrap(mainLayout);
            measureSizeAsMatch(mainLayout);
            mainLayout.remeasureMatchParentChildViewWidth();
            mainLayout.remeasureMatchParentChildViewHeight();
            measureLocation(mainLayout);
        } else if (mainView != null) {
            measureAllViewSizeAsValue(mainView);
            measureLayoutSizeAsWrap(mainView);
            measureSizeAsMatch(mainView);
            measureLocation(mainView);
        } else System.err.println("资源文件未加载或不存在。");
        Log.d(this, "end");
    }

    public void invalidate(BaseLayout startLayout) {
        if (startLayout != null) {
            measureAllViewSizeAsValue(startLayout);
            measureLayoutSizeAsWrap(startLayout);
            measureSizeAsMatch(startLayout);
            startLayout.remeasureMatchParentChildViewWidth();
            startLayout.remeasureMatchParentChildViewHeight();
            measureLocation(startLayout);
        } else System.err.println("父节点的Layout不能为空。");
        Log.d(this, "end");
    }

    /**
     * 首先测量可以有确定值设置的控件（Layout、View）
     */
    private void measureAllViewSizeAsValue(View item) {
        if (item.getAttribute().getVisible() == BaseAttribute.GONE) {
            setWidth(item, 0);
            setHeight(item, 0);
            return;
        }
        if (isWidthAsValue(item)) {
            item.getComponent().setSize(item.getAttribute().getWidth(), item.getHeight());
        }
        if (isHeightAsValue(item)) {
            item.getComponent().setSize(item.getWidth(), item.getAttribute().getHeight());
        }
        if (item instanceof BaseLayout) {
            // 当前节点是Layout，继续深入测量
            measureSizeAsValueInLayout((BaseLayout) item);
        } else {
            // 测量这个子控件的大小是否是wrap
            measureNormalViewSizeAsWrap(item);
        }
    }

    /**
     * 测量以layout为节点的子控件的确定值
     *
     * @param layout
     */
    private void measureSizeAsValueInLayout(BaseLayout layout) {
        if (layout.getAttribute().getVisible() == BaseAttribute.GONE) {
            setWidth(layout, 0);
            setHeight(layout, 0);
            return;
        }
        sonViews = layout.getSonViews();
        for (View son : sonViews) {
            if (son instanceof BaseLayout) {
                measureSizeAsValueInLayout((BaseLayout) son);
            } else {
                measureAllViewSizeAsValue(son);
            }
        }
    }

    /**
     * 测量不是Layout节点的wrap属性的大小
     *
     * @param item
     */
    private void measureNormalViewSizeAsWrap(View item) {
        if (item.getAttribute().getVisible() == BaseAttribute.GONE) {
            setWidth(item, 0);
            setHeight(item, 0);
            return;
        }
        // 按节点View允许最小的大小进行设置
        if (isWidthAsWrap(item)) {
            item.getComponent().setSize(item.getMinWidth(), item.getHeight());
        }
        if (isHeightAsWrap(item)) {
            item.getComponent().setSize(item.getWidth(), item.getMinHeight());
        }
        Log.d("measureNormalViewSizeAsWrap", item.getAttribute().getId() + ", " + item.getComponent().getSize());
    }

    /**
     * 测量大小为Wrap的Layout
     *
     * @param layout
     */
    private void measureLayoutSizeAsWrap(BaseLayout layout) {
        if (layout.getAttribute().getVisible() == BaseAttribute.GONE) {
            setWidth(layout, 0);
            setHeight(layout, 0);
            return;
        }
        // layout的布局方向
        boolean vertical = isVertical(layout), horizontal = isHorizontal(layout);
        // layout是否是wrap的
        boolean widthWrap = isWidthAsWrap(layout), heightWrap = isHeightAsWrap(layout);
        // 如果是wrap的话，layout最终应该设置为最大的值
        int maxWidth = 0, maxHeight = 0;
        sonViews = layout.getSonViews();
        for (View son : sonViews) {
            if (son == null) continue;
            if (son.getSonViews().size() > 0 && son instanceof BaseLayout) {
                // 这个子控件是一个layout
                measureLayoutSizeAsWrap((BaseLayout) son);
                if (widthWrap) {
                    if (vertical) {
                        // 垂直布局，宽度以子控件宽度最大值为准
                        if (maxWidth < son.getWidth()) {
                            maxWidth = son.getWidth();
                        }
                    } else {
                        // 横向布局，宽度以所有子控件宽度累加为准
                        maxWidth += son.getWidth();
                    }
                }
                if (heightWrap) {
                    if (vertical) {
                        // 垂直布局，高度以所有子控件高度累加为准
                        if (isHeightAsWrap(son) || isHeightAsMatch(son)) {
                            maxHeight += son.getMinHeight();
                        } else {
                            maxHeight += son.getHeight();
                        }
                    } else {
                        // 横向布局，高度以所有子控件高度最大值为准
                        if (maxHeight < son.getHeight()) {
                            maxHeight = son.getHeight();
                        }
                    }
                }
            } else {
                // 这个子控件是一个普通View
                if (widthWrap) {
                    measureSonNormalViewWidthMatchToWrap(son);
                    if (vertical) {
                        // 垂直布局，宽度以子控件宽度最大值为准
                        if (maxWidth < son.getWidth()) {
                            maxWidth = son.getWidth();
                        }
                    } else {
                        // 横向布局，宽度以所有子控件宽度累加为准
                        maxWidth += son.getWidth();
                    }
                }
                if (heightWrap) {
                    measureSonNormalViewHeightMatchToWrap(son);
                    if (vertical) {
                        // 垂直布局，高度以所有子控件高度累加为准
                        if (isHeightAsWrap(son) || isHeightAsMatch(son)) {
                            maxHeight += son.getMinHeight();
                            setHeight(son, son.getMinHeight());
                        } else {
                            maxHeight += son.getHeight();
                        }
                    } else {
                        // 横向布局，高度以所有子控件高度最大值为准
                        if (maxHeight < son.getHeight()) {
                            maxHeight = son.getHeight();
                        }
                    }
                }
            }
        }
        // 遍历完当前节点的所有子节点，开始设置当前节点的宽高（如果属性是wrap）。
        if (widthWrap) {
            setWidth(layout, maxWidth);
            // 如果是wrap，需要修改子节点为match的view宽。
            remeasureSonWidthAsMatch(layout);
        }
        if (heightWrap) {
            setHeight(layout, maxHeight);
            // 如果是wrap，需要修改子节点为match的view高。
            remeasureSonHeightAsMatch(layout);
        }
        Log.d("measureLayoutSizeAsWrap", layout.getAttribute().getId() + ", " + layout.getComponent().getSize());
    }

    /**
     * 当父节点的宽度是wrap时，且父节点指定布局方向为vertical，最后需要重置子控件宽度为match的值
     *
     * @param layout
     */
    private void remeasureSonWidthAsMatch(BaseLayout layout) {
        if (layout.getAttribute().getVisible() == BaseAttribute.GONE) {
            setWidth(layout, 0);
            return;
        }
        if (isVertical(layout)) {
            // 重置子节点为match的值
            sonViews = layout.getSonViews();
            for (View son : sonViews) {
                if (son == null) continue;
                if (isWidthAsMatch(son)) {
                    setWidth(son, layout.getWidth());
                }
                if (son instanceof BaseLayout) {
                    remeasureSonWidthAsMatch((BaseLayout) son);
                }
            }
        }
    }

    /**
     * 当父节点的高度是wrap时，且父节点指定布局方向为horizontal，最后需要重置子控件高度为match的值
     *
     * @param layout
     */
    private void remeasureSonHeightAsMatch(BaseLayout layout) {
        if (layout.getAttribute().getVisible() == BaseAttribute.GONE) {
            setHeight(layout, 0);
            return;
        }
        if (isHorizontal(layout)) {
            // 重置子节点为match的值
            sonViews = layout.getSonViews();
            for (View son : sonViews) {
                if (son == null) continue;
                if (isHeightAsMatch(son)) {
                    setHeight(son, layout.getHeight());
                }
                if (son instanceof BaseLayout) {
                    remeasureSonHeightAsMatch((BaseLayout) son);
                }
            }
        }
    }

    /**
     * 当父Layout宽度属性为wrap时，非Layout的子控件为match需要转换为wrap
     *
     * @param son Layout宽度为wrap的子控件
     */
    private void measureSonNormalViewWidthMatchToWrap(View son) {
        if (son.getAttribute().getVisible() == BaseAttribute.GONE) {
            setWidth(son, 0);
            return;
        }
        if (!isWidthAsValue(son)) {
            // 只有子View没有指定确定值时才设置为wrap
            setWidth(son, son.getMinWidth());
        }
    }

    /**
     * 当父Layout高度属性为wrap时，非Layout的子控件为match需要转换为wrap
     *
     * @param son Layout宽度为match的子控件
     */
    private void measureSonNormalViewHeightMatchToWrap(View son) {
        if (son.getAttribute().getVisible() == BaseAttribute.GONE) {
            setHeight(son, 0);
            return;
        }
        if (!isHeightAsValue(son)) {
            // 只有子View没有指定确定值时才设置为wrap
            setHeight(son, son.getMinHeight());
        }
    }

    /**
     * 测量节点为match时的宽高大小
     *
     * @param item
     */
    private void measureSizeAsMatch(View item) {
        item.invalidate();
        if (item.getParent() != null) {
            measureViewWidthAsMatch((BaseLayout) item.getParent(), item);
            measureViewHeightAsMatch((BaseLayout) item.getParent(), item);
        }
        if (item.getAttribute().getVisible() == BaseAttribute.GONE) {
            setWidth(item, 0);
            setHeight(item, 0);
            return;
        }
        if (item instanceof BaseLayout) {
            if (item.getParent() != null) {
                ((BaseLayout) item.getParent()).remeasureMatchParentChildViewWidth();
                ((BaseLayout) item.getParent()).remeasureMatchParentChildViewHeight();
            }
            sonViews = item.getSonViews();
            for (View son : sonViews) {
                if (son == null) continue;
                measureSizeAsMatch(son);
            }
            // 只有子节点都遍历结束才刷新match属性，防止抖动
            ((BaseLayout) item).remeasureMatchParentChildViewWidth();
            ((BaseLayout) item).remeasureMatchParentChildViewHeight();
        }
        Log.d("measureSizeAsMatch", item.getAttribute().getId() + ", " + item.getComponent().getSize());
    }

    /**
     * 测量以parent为父节点的子View，属性为match时的宽
     *
     * @param parent 父节点
     * @param son    子View（可能也是一个layout）
     */
    private void measureViewWidthAsMatch(BaseLayout parent, View son) {
        if (son.getAttribute().getVisible() == BaseAttribute.GONE) {
            setWidth(son, 0);
            return;
        }
        if (isHorizontal(parent)) {
            if (isWidthAsMatch(parent) || isWidthAsValue(parent)) {
                if (isWidthAsMatch(son)) {
                    parent.matchParentWidthViews.add(son);
                } else {
                    parent.currNoMatchWidth += son.getWidth()
                            + son.getAttribute().getMarginLeft()
                            + son.getAttribute().getMarginRight();
                }
            } else {
                // 父布局的宽度是wrap，说明：这个son的宽度也已被按照wrap处理了，所以match在这里是无效的
            }
        } else if (isVertical(parent)) {
            if (isWidthAsMatch(son)) {
                setWidth(son, parent.getWidth());
            }
        }
    }

    /**
     * 测量以parent为父节点的子View，属性为match时的高
     *
     * @param parent 父节点
     * @param son    子View（可能也是一个layout）
     */
    private void measureViewHeightAsMatch(BaseLayout parent, View son) {
        if (son.getAttribute().getVisible() == BaseAttribute.GONE) {
            setHeight(son, 0);
            return;
        }
        if (isVertical(parent)) {
            if (isHeightAsMatch(parent) || isHeightAsValue(parent)) {
                if (isHeightAsMatch(son)) {
                    parent.matchParentHeightViews.add(son);
                } else {
                    parent.currNoMatchHeight += son.getHeight()
                            + son.getAttribute().getMarginTop()
                            + son.getAttribute().getMarginBottom();
                }
            } else {
                // 父布局的高度是wrap，说明：这个son的高度也已被按照wrap处理了，所以match在这里是无效的
            }
        } else if (isHorizontal(parent)) {
            if (isHeightAsMatch(son)) {
                setHeight(son, parent.getHeight());
            }
        }
    }

    /**
     * 当所有节点的大小都测量完成之后，需要根据布局方向来确定各个节点View的位置
     *
     * @param parent 一个父节点，根据这个父节点，查找子节点并设置位置
     */
    private void measureLocation(View parent) {
        if (parent.getAttribute().getVisible() == BaseAttribute.GONE) return;
        int startX = 0, startY = 0;
        sonViews = parent.getSonViews();
        for (View son : sonViews) {
            if (son == null) continue;
            if (son.getAttribute().getVisible() == BaseAttribute.GONE) continue;
            if (son.getParent() != null) {
                if (isVertical((BaseLayout) son.getParent())) {
                    int width = son.getWidth(), height = son.getHeight();
                    if (son.getAttribute().getWidth() == ViewAttribute.MATCH_PARENT) {
                        width = son.getWidth() - son.getAttribute().getMarginLeft() - son.getAttribute().getMarginRight();
                        setWidth(son, width);
                    }
                    if (son.getAttribute().getHeight() == ViewAttribute.MATCH_PARENT) {
                        height = son.getHeight() - son.getAttribute().getMarginTop() - son.getAttribute().getMarginBottom();
                        setHeight(son, height);
                    }
                    startY += son.getAttribute().getMarginTop();
                    if (son.getPreView() != null && son.getPreView().getVisible()) {
                        startY += son.getPreView().getAttribute().getMarginBottom();
                    }
                    int marginLeft = son.getAttribute().getMarginLeft();
                    son.getComponent().setLocation(startX + marginLeft, startY);
                    startY += son.getHeight();
                }
                if (isHorizontal((BaseLayout) son.getParent())) {
                    int marginTop = son.getAttribute().getMarginTop();
                    if (son.getAttribute().getHeight() == ViewAttribute.MATCH_PARENT) {
                        int height = son.getHeight() - son.getAttribute().getMarginTop() - son.getAttribute().getMarginBottom();
                        setHeight(son, height);
                    }
                    if (son.getAttribute().getWidth() == ViewAttribute.MATCH_PARENT) {
                        int width = son.getWidth() - son.getAttribute().getMarginLeft() - son.getAttribute().getMarginRight();
                        setWidth(son, width);
                    }
                    startX += son.getAttribute().getMarginLeft();
                    if (son.getPreView() != null && son.getPreView().getVisible()) {
                        startX += son.getPreView().getAttribute().getMarginRight();
                    }
                    son.getComponent().setLocation(startX, startY + marginTop);
                    startX += son.getWidth();
                }
            } else {
                son.getComponent().setLocation(son.getAttribute().getStartX(), son.getAttribute().getStartY());
            }
            if (son instanceof BaseLayout) {
                measureLocation(son);
            }
            Log.d("measureLocation", son.getAttribute().getId() + ", " + son.getComponent().getLocation());
        }
    }

    /*********************  一些常用方法  *********************/

    private void setWidth(View view, int width) {
        view.getComponent().setSize(width, view.getHeight());
    }

    private void setHeight(View view, int height) {
        view.getComponent().setSize(view.getWidth(), height);
    }

    private boolean isVertical(BaseLayout layout) {
        return layout.getAttribute().getOrientation() == LayoutAttribute.VERTICAL;
    }

    private boolean isHorizontal(BaseLayout layout) {
        return layout.getAttribute().getOrientation() == LayoutAttribute.HORIZONTAL;
    }

    private boolean isWidthAsValue(View view) {
        return view.getAttribute().getWidth() != BaseAttribute.WRAP_CONTENT && view.getAttribute().getWidth() != BaseAttribute.MATCH_PARENT;
    }

    private boolean isHeightAsValue(View view) {
        return view.getAttribute().getHeight() != BaseAttribute.WRAP_CONTENT && view.getAttribute().getHeight() != BaseAttribute.MATCH_PARENT;
    }

    private boolean isWidthAsWrap(View view) {
        return view.getAttribute().getWidth() == BaseAttribute.WRAP_CONTENT;
    }

    private boolean isHeightAsWrap(View view) {
        return view.getAttribute().getHeight() == BaseAttribute.WRAP_CONTENT;
    }

    private boolean isWidthAsMatch(View view) {
        return view.getAttribute().getWidth() == BaseAttribute.MATCH_PARENT;
    }

    private boolean isHeightAsMatch(View view) {
        return view.getAttribute().getHeight() == BaseAttribute.MATCH_PARENT;
    }

}
