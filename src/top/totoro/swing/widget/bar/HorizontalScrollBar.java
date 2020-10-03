package top.totoro.swing.widget.bar;

import top.totoro.swing.widget.base.BaseScrollBar;

import java.awt.*;

public class HorizontalScrollBar extends BaseScrollBar.Horizontal {

    private Color c1 = Color.decode("#d8d8d8");
    private Color c2 = Color.decode("#cbcbcb");
    private Color c3 = Color.decode("#c7c7c7");
    private Color c4 = Color.decode("#bababa");

    private int height = 0;

    public HorizontalScrollBar() {
        setHeight(15);
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    protected void drawBarHeadAndTail(Graphics graphics) {
        Color origin = graphics.getColor();
        int gap1 = barX + 1, gap2 = barX + 2, gap3 = barX + 3;

        drawPoint(c1, gap1, 5);
        drawPoint(c2, gap2, 5);
        drawPoint(c3, gap3, 5);

        drawPoint(c1, barX, 6);
        drawPoint(c3, gap1, 6);
        drawPoint(c4, gap2, 6);
        drawPoint(c4, gap3, 6);

        drawPoint(c1, barX, 7);
        drawPoint(c3, gap1, 7);
        drawPoint(c4, gap2, 7);
        drawPoint(c4, gap3, 7);

        drawPoint(c2, barX, 8);
        drawPoint(c4, gap1, 8);
        drawPoint(c4, gap2, 8);
        drawPoint(c4, gap3, 8);

        drawPoint(c3, barX, 9);
        drawPoint(c4, gap1, 9);
        drawPoint(c4, gap2, 9);
        drawPoint(c4, gap3, 9);

        drawPoint(c1, gap1, 14);
        drawPoint(c2, gap2, 14);
        drawPoint(c3, gap3, 14);

        drawPoint(c1, barX, 13);
        drawPoint(c3, gap1, 13);
        drawPoint(c4, gap2, 13);
        drawPoint(c4, gap3, 13);

        drawPoint(c1, barX, 12);
        drawPoint(c3, gap1, 12);
        drawPoint(c4, gap2, 12);
        drawPoint(c4, gap3, 12);

        drawPoint(c2, barX, 11);
        drawPoint(c4, gap1, 11);
        drawPoint(c4, gap2, 11);
        drawPoint(c4, gap3, 11);

        drawPoint(c3, barX, 10);
        drawPoint(c4, gap1, 10);
        drawPoint(c4, gap2, 10);
        drawPoint(c4, gap3, 10);

        int gap0 = barX + barWidth - 1;
        gap1 = barX + barWidth - 2;
        gap2 = barX + barWidth - 3;
        gap3 = barX + barWidth - 4;

        drawPoint(c1, gap1, 5);
        drawPoint(c2, gap2, 5);
        drawPoint(c3, gap3, 5);

        drawPoint(c1, gap0, 6);
        drawPoint(c3, gap1, 6);
        drawPoint(c4, gap2, 6);
        drawPoint(c4, gap3, 6);

        drawPoint(c1, gap0, 7);
        drawPoint(c3, gap1, 7);
        drawPoint(c4, gap2, 7);
        drawPoint(c4, gap3, 7);

        drawPoint(c2, gap0, 8);
        drawPoint(c4, gap1, 8);
        drawPoint(c4, gap2, 8);
        drawPoint(c4, gap3, 8);

        drawPoint(c3, gap0, 9);
        drawPoint(c4, gap1, 9);
        drawPoint(c4, gap2, 9);
        drawPoint(c4, gap3, 9);

        drawPoint(c1, gap1, 14);
        drawPoint(c2, gap2, 14);
        drawPoint(c3, gap3, 14);

        drawPoint(c1, gap0, 13);
        drawPoint(c3, gap1, 13);
        drawPoint(c4, gap2, 13);
        drawPoint(c4, gap3, 13);

        drawPoint(c1, gap0, 12);
        drawPoint(c3, gap1, 12);
        drawPoint(c4, gap2, 12);
        drawPoint(c4, gap3, 12);

        drawPoint(c2, gap0, 11);
        drawPoint(c4, gap1, 11);
        drawPoint(c4, gap2, 11);
        drawPoint(c4, gap3, 11);

        drawPoint(c3, gap0, 10);
        drawPoint(c4, gap1, 10);
        drawPoint(c4, gap2, 10);
        drawPoint(c4, gap3, 10);

        graphics.setColor(origin);
    }

    @Override
    protected void drawMiddle(Graphics graphics) {
        drawLine(c4, barX + 4, 5, barWidth - 8, 10);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(barWidth, getHeight());
    }

}
