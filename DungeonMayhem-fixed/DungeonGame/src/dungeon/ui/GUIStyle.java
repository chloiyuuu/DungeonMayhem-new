package dungeon.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GUIStyle {

    public static final Color BG_MAIN       = new Color(10, 10, 10);
    public static final Color BG_PANEL      = new Color(20, 20, 20);
    public static final Color CARD_BORDER   = new Color(40, 40, 40);

    public static final Color TEXT_MAIN     = new Color(230, 230, 230);
    public static final Color TEXT_MUTED    = new Color(125, 125, 125);

    public static final Color ACCENT_BLUE   = new Color(59, 130, 246);
    public static final Color ACCENT_GREEN  = new Color(16, 185, 129);
    public static final Color ACCENT_RED    = new Color(239, 68, 68);
    public static final Color ACCENT_ORANGE = new Color(245, 158, 11);
    public static final Color ACCENT_PURPLE = new Color(139, 92, 246);

    public static final Color BTN_NORMAL    = new Color(30, 30, 30);
    public static final Color BTN_HOVER     = new Color(50, 50, 50);
    public static final Color BTN_TEXT      = new Color(230, 230, 230);
    public static final Color BTN_PRIMARY   = new Color(40, 100, 200);
    public static final Color BTN_PRIMARY_H = new Color(60, 120, 220);

    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 48);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    public static JButton createStyledButton(String text, boolean primary) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(primary ? BTN_PRIMARY_H.darker() : BTN_HOVER.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(primary ? BTN_PRIMARY_H : BTN_HOVER);
                } else {
                    g2.setColor(getBackground());
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                FontMetrics fm = g2.getFontMetrics();
                Rectangle stringBounds = fm.getStringBounds(this.getText(), g2).getBounds();
                int textX = (getWidth() - stringBounds.width) / 2;
                int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();
                g2.setColor(getForeground());
                g2.setFont(getFont());
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setBackground(primary ? BTN_PRIMARY : BTN_NORMAL);
        btn.setForeground(primary ? Color.WHITE : BTN_TEXT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
        return btn;
    }

    public static JButton createStyledButton(String text, int width, int height) {
        JButton btn = createStyledButton(text, false);
        btn.setPreferredSize(new Dimension(width, height));
        return btn;
    }

    public static JToggleButton createStyledToggleButton(String text) {
        JToggleButton btn = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(BTN_HOVER.darker());
                } else if (isSelected()) {
                    g2.setColor(BTN_PRIMARY.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(BTN_HOVER);
                } else {
                    g2.setColor(getBackground());
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                FontMetrics fm = g2.getFontMetrics();
                Rectangle stringBounds = fm.getStringBounds(this.getText(), g2).getBounds();
                int textX = (getWidth() - stringBounds.width) / 2;
                int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();
                g2.setColor(isSelected() ? Color.WHITE : getForeground());
                g2.setFont(getFont());
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setBackground(BTN_NORMAL);
        btn.setForeground(BTN_TEXT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JLabel createStyledLabel(String text, Color color, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(color);
        lbl.setFont(font);
        return lbl;
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }
}
