package top.totoro.swing.widget.bar;

import top.totoro.swing.widget.base.BaseScrollBar;

import java.awt.*;

public class VerticalScrollBar extends BaseScrollBar.Vertical {

    private Color c1 = Color.decode("#d8d8d8");
    private Color c2 = Color.decode("#cbcbcb");
    private Color c3 = Color.decode("#c7c7c7");
    private Color c4 = Color.decode("#bababa");

    private int width = 0;

    public VerticalScrollBar() {
        setWidth(10);
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    protected void drawBarHeadAndTail(Graphics graphics) {
        Color origin = graphics.getColor();
        int gap1 = barY + 1, gap2 = barY + 2, gap3 = barY + 3;

        drawPoint(c1, 0, gap1);
        drawPoint(c2, 0, gap2);
        drawPoint(c3, 0, gap3);

        drawPoint(c1, 1, barY);
        drawPoint(c3, 1, gap1);
        drawPoint(c4, 1, gap2);
        drawPoint(c4, 1, gap3);

        drawPoint(c1, 2, barY);
        drawPoint(c3, 2, gap1);
        drawPoint(c4, 2, gap2);
        drawPoint(c4, 2, gap3);

        drawPoint(c2, 3, barY);
        drawPoint(c4, 3, gap1);
        drawPoint(c4, 3, gap2);
        drawPoint(c4, 3, gap3);

        drawPoint(c3, 4, barY);
        drawPoint(c4, 4, gap1);
        drawPoint(c4, 4, gap2);
        drawPoint(c4, 4, gap3);

        drawPoint(c1, 9, gap1);
        drawPoint(c2, 9, gap2);
        drawPoint(c3, 9, gap3);

        drawPoint(c1, 8, barY);
        drawPoint(c3, 8, gap1);
        drawPoint(c4, 8, gap2);
        drawPoint(c4, 8, gap3);

        drawPoint(c1, 7, barY);
        drawPoint(c3, 7, gap1);
        drawPoint(c4, 7, gap2);
        drawPoint(c4, 7, gap3);

        drawPoint(c2, 6, barY);
        drawPoint(c4, 6, gap1);
        drawPoint(c4, 6, gap2);
        drawPoint(c4, 6, gap3);

        drawPoint(c3, 5, barY);
        drawPoint(c4, 5, gap1);
        drawPoint(c4, 5, gap2);
        drawPoint(c4, 5, gap3);

        int gap0 = barY + barHeight - 1;
        gap1 = barY + barHeight - 2;
        gap2 = barY + barHeight - 3;
        gap3 = barY + barHeight - 4;

        drawPoint(c1, 0, gap1);
        drawPoint(c2, 0, gap2);
        drawPoint(c3, 0, gap3);

        drawPoint(c1, 1, gap0);
        drawPoint(c3, 1, gap1);
        drawPoint(c4, 1, gap2);
        drawPoint(c4, 1, gap3);

        drawPoint(c1, 2, gap0);
        drawPoint(c3, 2, gap1);
        drawPoint(c4, 2, gap2);
        drawPoint(c4, 2, gap3);

        drawPoint(c2, 3, gap0);
        drawPoint(c4, 3, gap1);
        drawPoint(c4, 3, gap2);
        drawPoint(c4, 3, gap3);

        drawPoint(c3, 4, gap0);
        drawPoint(c4, 4, gap1);
        drawPoint(c4, 4, gap2);
        drawPoint(c4, 4, gap3);

        drawPoint(c1, 9, gap1);
        drawPoint(c2, 9, gap2);
        drawPoint(c3, 9, gap3);

        drawPoint(c1, 8, gap0);
        drawPoint(c3, 8, gap1);
        drawPoint(c4, 8, gap2);
        drawPoint(c4, 8, gap3);

        drawPoint(c1, 7, gap0);
        drawPoint(c3, 7, gap1);
        drawPoint(c4, 7, gap2);
        drawPoint(c4, 7, gap3);

        drawPoint(c2, 6, gap0);
        drawPoint(c4, 6, gap1);
        drawPoint(c4, 6, gap2);
        drawPoint(c4, 6, gap3);

        drawPoint(c3, 5, gap0);
        drawPoint(c4, 5, gap1);
        drawPoint(c4, 5, gap2);
        drawPoint(c4, 5, gap3);

        graphics.setColor(origin);
    }

    @Override
    protected void drawMiddle(Graphics graphics) {
        drawLine(c4, 0, barY + 4, 10, barHeight - 8);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), barHeight);
    }

}
