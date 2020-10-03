package top.totoro.swing.widget.base;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class BaseScrollBar extends JComponent {

    private Graphics graphics;
    private JComponent component;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private boolean visible = false;

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.visible = visible;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setMouseListener(MouseListener mouseListener) {
        this.mouseListener = mouseListener;
    }

    public void setMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = mouseMotionListener;
    }

    @Override
    public void paint(Graphics g) {
        if (getVisible()) {
            if (component == null) {
                component = this;
                component.addMouseListener(mouseListener);
                component.addMouseMotionListener(mouseMotionListener);
            }
            graphics = g;
            drawBarHeadAndTail(g);
            drawMiddle(g);
        }
    }

    public static abstract class Horizontal extends top.totoro.swing.widget.base.BaseScrollBar {
        protected int barX = 0;
        protected int barWidth = 0;

        public void setBarX(int x) {
            this.barX = x;
        }

        public void setBarWidth(int barWidth) {
            this.barWidth = barWidth;
        }

        public int getBarX() {
            return barX;
        }

        public int getBarWidth() {
            return barWidth;
        }

        /**
         * 设置Bar的容器高度，而
         *
         * @param height 高度，不一定是滑条的高度，但要不小于滑条的高度
         */
        public abstract void setHeight(int height);

        public abstract int getHeight();

    }

    public static abstract class Vertical extends top.totoro.swing.widget.base.BaseScrollBar {
        protected int barY = 0;
        protected int barHeight = 0;

        public void setBarY(int y) {
            barY = y;
        }

        public void setBarHeight(int height) {
            barHeight = height;
        }

        public int getBarY() {
            return barY;
        }

        public int getBarHeight() {
            return barHeight;
        }

        /**
         * 设置Bar的容器宽度
         *
         * @param width 宽度，不一定是滑条的宽度，但要不小于滑条的宽度
         */
        public abstract void setWidth(int width);

        public abstract int getWidth();
    }

    protected abstract void drawBarHeadAndTail(Graphics graphics);

    protected abstract void drawMiddle(Graphics graphics);

    protected void drawPoint(Color c, int x, int y) {
        Color origin = graphics.getColor();
        graphics.setColor(c);
        graphics.drawLine(x, y, x, y);
        graphics.setColor(origin);
    }

    protected void drawLine(Color c, int x, int y, int width, int height) {
        Color origin = graphics.getColor();
        graphics.setColor(c);
        for (int i = 0; i < width; i++) {
            graphics.drawLine(x + i, y, x + i, y + height);
        }
        graphics.setColor(origin);
    }
}
