package org.javaopensoft.theme;

import javax.swing.*;
import java.awt.*;

public class WProgressBar extends JProgressBar {

    private final int arc = 20;   // roundness of corners
    private final int gap = 4;    // gap between border and progress

    public WProgressBar(int min, int max) {
        setMinimum(min);
        setMaximum(max);
        setOpaque(false);
        setForeground(Color.RED);  // finished part
        setBackground(new Color(0, 0, 0, 0));  // transparent unfinished part
        setBorderPainted(false);   // custom border
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Draw border
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

        // Calculate progress width
        int progressWidth = (int) ((width - 2 * gap) * getPercentComplete());

        // Draw progress
        g2.setColor(Color.RED);
        g2.fillRoundRect(gap, gap, progressWidth, height - 2 * gap, arc - gap, arc - gap);

        g2.dispose();
    }
}
