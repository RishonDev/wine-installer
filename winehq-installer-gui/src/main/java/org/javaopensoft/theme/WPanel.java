package org.javaopensoft.theme;

import org.javaopensoft.WINE;

import javax.swing.*;
import java.util.Objects;

public class WPanel extends JPanel {
    private final JLabel background;

    public WPanel() {
        setLayout(null); // manual positioning

        // Background label
        background = new JLabel(new ImageIcon(
                Objects.requireNonNull(WINE.class.getResource("bg2.png"))));
        background.setBounds(0, 0, 965, 540);

        add(background); // add background first
    }

    public void addComponent(JComponent component) {
        component.setOpaque(false);
        add(component);

        // Keep background at back
        setComponentZOrder(background, getComponentCount() - 1);

        // Refresh panel
        revalidate();
        repaint();
    }
}
