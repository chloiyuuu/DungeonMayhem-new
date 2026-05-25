package dungeon.ui;

import javax.swing.*;
import java.awt.*;

public class HealthBar extends JPanel {
    private int currentValue;
    private int maxValue;
    private Color barColor;
    private boolean showText;
    private String prefix;
    private int customFontSize = -1;
    private int customBarHeight = -1;

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

    public void setCustomFontSize(int size) {
        this.customFontSize = size;
        repaint();
    }

    public void setCustomBarHeight(int height) {
        this.customBarHeight = height;
        repaint();
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
        int barHeight = customBarHeight != -1 ? customBarHeight : (showText ? 12 : Math.min(height, 40));
        int fontSize = customFontSize != -1 ? customFontSize : 12;

        int textHeight = showText ? fontSize + 4 : 0;
        int yOffset = showText ? textHeight + 4 : (height - barHeight) / 2;


        if (showText && customFontSize != -1) {
            yOffset = textHeight + (height - textHeight - barHeight) / 2;
        } else if (showText) {
            yOffset = height - barHeight;
        }

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
            g2.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            g2.setColor(GUIStyle.TEXT_MUTED);
            int tx = 0;
            int ty = yOffset - 4;

            g2.drawString(text, tx, ty);
        }

        g2.dispose();
    }
}
