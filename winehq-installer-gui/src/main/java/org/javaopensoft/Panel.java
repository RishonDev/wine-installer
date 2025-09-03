package org.javaopensoft;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Panel extends JPanel {
    private final JLabel background;

    public Panel() {
        setLayout(null); // manual positioning

        // Background label
        background = new JLabel(new ImageIcon(
                Objects.requireNonNull(Main.class.getResource("bg.jpg"))));
        background.setBounds(0, 0, 643, 360);

        // Add background FIRST so it stays at the back
        add(background);
    }

    public void addComponent(JComponent component) {
        component.setOpaque(false);
        add(component);
        component.setVisible(true);

        // Ensure background always stays behind
        setComponentZOrder(background, getComponentCount() - 1);
    }
}
