package dungeon.ui;

import javax.swing.*;
import java.awt.*;

public class HealthBar extends JPanel {
    private int currentValue;
    private int maxValue;
    private Color barColor;
    private boolean showText;
    private String prefix;

    public HealthBar() {
        this(100, 100, GUIStyle.ACCENT_GREEN, true, "");
    }

    public HealthBar(int current, int max, Color color, boolean showText, String prefix) {
        this.currentValue = current;
        this.maxValue = max;
        this.barColor = color;
        this.showText = showText;
        this.prefix = prefix;
        setPreferredSize(new Dimension(200, showText ? 24 : 12));
        setOpaque(false);
    }

    public void updateHealth(int current, int max) {
        this.currentValue = Math.max(0, current);
        this.maxValue = Math.max(1, max);
        repaint();
    }

    public void setBarColor(Color color) {
        this.barColor = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int barHeight = showText ? 8 : height;
        int yOffset = showText ? height - barHeight : 0;

        g2.setColor(GUIStyle.BTN_HOVER);
        g2.fillRoundRect(0, yOffset, width, barHeight, barHeight, barHeight);

        float ratio = Math.min(1.0f, (float) currentValue / maxValue);
        int fillWidth = (int) (width * ratio);

        if (fillWidth > 0) {
            g2.setColor(barColor);
            g2.fillRoundRect(0, yOffset, fillWidth, barHeight, barHeight, barHeight);
        }

        if (showText) {
            String text = prefix + currentValue + " / " + maxValue;
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(GUIStyle.TEXT_MUTED);
            FontMetrics fm = g2.getFontMetrics();
            int tx = 0;
            int ty = yOffset - 4;

            g2.drawString(text, tx, ty);
        }

        g2.dispose();
    }
}
