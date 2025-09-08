package org.javaopensoft.theme;

import javax.swing.*;
import java.awt.*;

public class WLabel extends JLabel {

    public WLabel() {
        init();
    }

    public WLabel(String text) {
        super(text);
        init();
    }

    private void init() {
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 16));
        setOpaque(false); // no background
        setBorder(null);  // no border
    }
}
