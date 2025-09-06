package org.javaopensoft.theme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

public class WProgressBar extends JProgressBar {

    public WProgressBar(int min, int max) {
        super(min, max);
        setOpaque(false);
        setBorderPainted(false);
        setForeground(new Color(200, 0, 0)); // fill color (red tone)
        setBackground(new Color(0, 0, 0, 100)); // dark translucent bg
        setFont(new Font("Arial", Font.BOLD, 14));
        setStringPainted(true); // show % text
        setUI(new WineProgressBarUI());
    }

    private static class WineProgressBarUI extends BasicProgressBarUI {
        private final int arc = 12; // roundness

        @Override
        protected void paintDeterminate(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = progressBar.getWidth();
            int height = progressBar.getHeight();

            // background
            g2.setColor(new Color(30, 30, 30, 180));
            g2.fillRoundRect(0, 0, width, height, arc, arc);

            // progress fill
            int filled = width * getAmountFull(null, width, height);
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(150, 0, 0),
                    width, 0, new Color(255, 50, 50)
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, filled, height, arc, arc);

            // border
            g2.setColor(new Color(200, 0, 0));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

            // text
            if (progressBar.isStringPainted()) {
                String text = progressBar.getString();
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                int x = (width - textWidth) / 2;
                int y = (height + textHeight) / 2 - 2;
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);
            }

            g2.dispose();
        }

        @Override
        protected void paintIndeterminate(Graphics g, JComponent c) {
            // fallback to determinate paint for indeterminate mode
            paintDeterminate(g, c);
        }
    }
}
