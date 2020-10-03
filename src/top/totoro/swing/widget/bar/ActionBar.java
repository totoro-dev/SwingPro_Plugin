package top.totoro.swing.widget.bar;

import top.totoro.swing.widget.base.DefaultAttribute;
import top.totoro.swing.widget.listener.OnActionBarClickListener;
import top.totoro.swing.widget.listener.OnActionBarResizeListener;
import top.totoro.swing.widget.listener.OnActivityDragListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ActionBar extends JComponent implements MouseListener, MouseMotionListener {

    private boolean canBack = false;
    private boolean canMin = false, canMid = true, canMax = false;

    // ActionBar的显示方向， HORIZONTAL : 水平， VERTICAL : 竖直
    public static final int HORIZONTAL = 1, VERTICAL = 2;

    private JComponent parent;
    private int orientation = HORIZONTAL;
    private Height height = Height.MID;
    private Color bg = Color.BLACK;
    private int iconColor = 1;
    private ImageIcon backIcon, minIcon, midIcon, maxIcon, closeIcon;
    private final JLabel back = new JLabel("", JLabel.CENTER);
    private final JLabel min = new JLabel("", JLabel.CENTER);
    private final JLabel mid = new JLabel("", JLabel.CENTER);
    private final JLabel max = new JLabel("", JLabel.CENTER);
    private final JLabel close = new JLabel("", JLabel.CENTER);
    private final JPanel content = new JPanel(null);
    private String titleText = "";
    private final JLabel title = new JLabel(titleText);
    private Point startDragLocation;
    private OnActionBarClickListener clickListener;
    private OnActivityDragListener dragListener;
    private OnActionBarResizeListener resizeListener;

    public ActionBar(JComponent parent) {
        this.parent = parent;
        parent.add(this);
        setBackground(Color.white);
        setLocation(0, 0);
        setLayout(null);
        add(back);
        add(min);
        add(mid);
        add(max);
        add(close);
        add(content);
        title.setLocation(10, 0);
        content.add(title);
        initListener();
        setBorder(1, Color.decode(DefaultAttribute.defaultBorderColor));
        setTitleColor(Color.decode(DefaultAttribute.defaultThemeColor));
    }

    private void initListener() {
        back.addMouseListener(this);
        min.addMouseListener(this);
        mid.addMouseListener(this);
        max.addMouseListener(this);
        close.addMouseListener(this);
        content.addMouseListener(this);
        content.addMouseMotionListener(this);
    }

    public void resize() {
        int width = parent.getWidth(), height = 0;
        if (this.height == Height.MIN) {
            height = 25;
            title.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        }
        if (this.height == Height.MID) {
            height = 30;
            title.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        }
        if (this.height == Height.MAX) {
            height = 40;
            title.setFont(new Font(Font.SERIF, Font.PLAIN, 21));
        }
        setSize(width, height);
        parent.setSize(width, height);
        back.setSize(height, height);
        min.setSize(height, height);
        mid.setSize(height, height);
        max.setSize(height, height);
        close.setSize(height, height);
        back.setLocation(0, 0);
        min.setLocation(width - 3 * height, 0);
        mid.setLocation(width - 2 * height, 0);
        max.setLocation(width - 2 * height, 0);
        close.setLocation(width - height, 0);
        back.setVisible(canBack);
        mid.setVisible(canMid);
        max.setVisible(canMax);
        if (canBack) {
            content.setSize(width - 4 * height, height);
            content.setLocation(height+1, 0);
        } else {
            content.setSize(width - 3 * height, height);
            content.setLocation(1, 0);
        }
        // 标题的文本框大小有可能改变
        setTitleText(titleText);
    }

    public void addOnActionBarClickListener(OnActionBarClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void addOnActivityDragListener(OnActivityDragListener dragListener) {
        this.dragListener = dragListener;
    }

    public void addOnActionBarResizeListener(OnActionBarResizeListener resizeListener) {
        this.resizeListener = resizeListener;
    }

    public void canMidScreen(boolean can) {
        canMid = can;
        canMax = !can;
        mid.setVisible(canMid);
        max.setVisible(canMax);
    }

    public void canBack(boolean can){
        canBack = can;
        resize();
    }

    /**
     * 设置ActionBar的高度：MIN  允许的最小值, MID  默认值, MAX  允许的最大值
     *
     * @param height 期望高度
     */
    public void setHeight(Height height) {
        if (resizeListener != null) {
            this.height = height;
            resize();
            resizeListener.onActionBarResize();
        }
        resetIcon();
    }

    // 不支持自定义宽度，必须占满父窗口的宽度
//    public void setWidth(int width){ }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
        title.setText(titleText);
        title.setSize(content.getWidth(), getHeight());
    }

    public void setTitleColor(Color color) {
        title.setForeground(color);
    }

    public int getOrientation() {
        return orientation;
    }

    /**
     * 设置ActionBar的方向
     *
     * @param orientation 方向
     *                    垂直{@link top.totoro.swing.widget.bar.ActionBar#VERTICAL}
     *                    水平{@link top.totoro.swing.widget.bar.ActionBar#HORIZONTAL}
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * 设置ActionBar的边框
     *
     * @param pixel 边框像素
     * @param color 边框颜色
     */
    public void setBorder(int pixel, Color color) {
        if (orientation == HORIZONTAL) {
            setBorder(BorderFactory.createMatteBorder(pixel, pixel, pixel, pixel, color));
            content.setBorder(BorderFactory.createMatteBorder(pixel, 0, pixel, 0, color));
        } else if (orientation == VERTICAL) {
            setBorder(BorderFactory.createMatteBorder(pixel, pixel, pixel, pixel, color));
            content.setBorder(BorderFactory.createMatteBorder(0, 0, 0, pixel, color));
        }
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        parent.setBackground(bg);
        content.setBackground(bg);
        this.bg = bg;
        resetIcon();
    }

    private void resetIcon() {
        int red = bg.getRed(), green = bg.getGreen(), blue = bg.getBlue();
        if (red > 150 && green > 150 && blue > 150) {
            iconColor = 1;
        } else {
            iconColor = 2;
        }
        if (iconColor == 1) {
            switch (height) {
                case MIN:
                    backIcon = new ImageIcon(getClass().getClassLoader().getResource("img/back_min.png"));
                    minIcon = new ImageIcon(getClass().getClassLoader().getResource("img/min_min.png"));
                    midIcon = new ImageIcon(getClass().getClassLoader().getResource("img/mid_min.png"));
                    maxIcon = new ImageIcon(getClass().getClassLoader().getResource("img/max_min.png"));
                    closeIcon = new ImageIcon(getClass().getClassLoader().getResource("img/close_min.png"));
                    break;
                case MID:
                    backIcon = new ImageIcon(getClass().getClassLoader().getResource("img/back_mid.png"));
                    minIcon = new ImageIcon(getClass().getClassLoader().getResource("img/min_mid.png"));
                    midIcon = new ImageIcon(getClass().getClassLoader().getResource("img/mid_mid.png"));
                    maxIcon = new ImageIcon(getClass().getClassLoader().getResource("img/max_mid.png"));
                    closeIcon = new ImageIcon(getClass().getClassLoader().getResource("img/close_mid.png"));
                    break;
                case MAX:
                    backIcon = new ImageIcon(getClass().getClassLoader().getResource("img/back_max.png"));
                    minIcon = new ImageIcon(getClass().getClassLoader().getResource("img/min_max.png"));
                    midIcon = new ImageIcon(getClass().getClassLoader().getResource("img/mid_max.png"));
                    maxIcon = new ImageIcon(getClass().getClassLoader().getResource("img/max_max.png"));
                    closeIcon = new ImageIcon(getClass().getClassLoader().getResource("img/close_max.png"));
                    break;
            }
        } else {
            switch (height) {
                case MIN:
                    backIcon = new ImageIcon(getClass().getClassLoader().getResource("img/back_min_white.png"));
                    minIcon = new ImageIcon(getClass().getClassLoader().getResource("img/min_min_white.png"));
                    midIcon = new ImageIcon(getClass().getClassLoader().getResource("img/mid_min_white.png"));
                    maxIcon = new ImageIcon(getClass().getClassLoader().getResource("img/max_min_white.png"));
                    closeIcon = new ImageIcon(getClass().getClassLoader().getResource("img/close_min_white.png"));
                    break;
                case MID:
                    backIcon = new ImageIcon(getClass().getClassLoader().getResource("img/back_mid_white.png"));
                    minIcon = new ImageIcon(getClass().getClassLoader().getResource("img/min_mid_white.png"));
                    midIcon = new ImageIcon(getClass().getClassLoader().getResource("img/mid_mid_white.png"));
                    maxIcon = new ImageIcon(getClass().getClassLoader().getResource("img/max_mid_white.png"));
                    closeIcon = new ImageIcon(getClass().getClassLoader().getResource("img/close_mid_white.png"));
                    break;
                case MAX:
                    backIcon = new ImageIcon(getClass().getClassLoader().getResource("img/back_max.png"));
                    minIcon = new ImageIcon(getClass().getClassLoader().getResource("img/min_max.png"));
                    midIcon = new ImageIcon(getClass().getClassLoader().getResource("img/mid_max.png"));
                    maxIcon = new ImageIcon(getClass().getClassLoader().getResource("img/max_max.png"));
                    closeIcon = new ImageIcon(getClass().getClassLoader().getResource("img/close_max.png"));
                    break;
            }
        }
        back.setIcon(backIcon);
        min.setIcon(minIcon);
        mid.setIcon(midIcon);
        max.setIcon(maxIcon);
        close.setIcon(closeIcon);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        if (clickListener != null) {
            if (source == back) {
                clickListener.onBackClick();
            } else if (source == min) {
                clickListener.onMinClick();
            } else if (source == mid) {
                canMidScreen(true);
                clickListener.onMidClick();
            } else if (source == max) {
                canMidScreen(false);
                clickListener.onMaxClick();
            } else if (source == close) {
                clickListener.onCloseClick();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == content) {
            startDragLocation = e.getPoint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() != content) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getSource() == content && dragListener != null) {
            dragListener.onActivityDrag(startDragLocation, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (e.getSource() != content) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    public enum Height {
        MIN, MID, MAX
    }

}
