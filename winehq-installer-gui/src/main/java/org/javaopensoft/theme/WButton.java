package org.javaopensoft.theme;

import javax.swing.*;
import java.awt.*;

public class WButton extends JButton {

    public WButton() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorderPainted(false); // custom painting instead
    }
    public WButton(String s) {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorderPainted(false); // custom painting instead
        setText(s);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 25; // roundness of corners

        if (getModel().isPressed()) {
            // Dark red fill on click
            g2.setColor(new Color(180, 0, 0, 120));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        } else if (getModel().isRollover()) {
            // Soft glow on hover
            g2.setColor(new Color(255, 0, 0, 60));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        }

        // Draw red border
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
