package org.javaopensoft.theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WCheckBox extends JCheckBox {

    private Color borderColor = new Color(200, 0, 0); // red outline
    private final Color hoverBorderColor = new Color(255, 80, 80); // lighter on hover
    private final Color checkColor = Color.WHITE; // tick color

    public WCheckBox(String text) {
        super(text);

        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 14));
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setIcon(new TransparentBox(false));
        setSelectedIcon(new TransparentBox(true));

        // Hover effect for outline
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                borderColor = hoverBorderColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                borderColor = new Color(200, 0, 0);
                repaint();
            }
        });
    }

    private class TransparentBox implements Icon {
        private final boolean selected;

        public TransparentBox(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Outer rounded square
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, getIconWidth() - 1, getIconHeight() - 1, 6, 6);

            // Tick if selected
            if (selected) {
                g2.setStroke(new BasicStroke(2.2f));
                g2.setColor(checkColor);
                g2.drawLine(x + 4, y + 8, x + 8, y + 12);
                g2.drawLine(x + 8, y + 12, x + 14, y + 4);
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 20;
        }
    }
}
