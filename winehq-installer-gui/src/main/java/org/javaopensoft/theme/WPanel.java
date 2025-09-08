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
