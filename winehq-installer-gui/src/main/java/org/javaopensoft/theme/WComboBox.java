package org.javaopensoft.theme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WComboBox<E> extends JComboBox<E> {

    private final Color borderColor = new Color(200, 0, 0);       // normal red
    private final Color hoverBorderColor = new Color(255, 80, 80); // lighter red when hovering
    private final Color backgroundColor = new Color(20, 20, 20, 180); // dark translucent
    private final Color hoverBackground = new Color(40, 40, 40, 200); // darker on hover
    private boolean isHovered = false;

    public WComboBox(E[] items) {
        super(items);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFocusable(false);

        // Transparent custom renderer for items
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setOpaque(true);
                label.setFont(new Font("Arial", Font.PLAIN, 14));
                if (isSelected) {
                    label.setBackground(new Color(200, 0, 0, 180));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(new Color(30, 30, 30, 180));
                    label.setForeground(Color.WHITE);
                }
                return label;
            }
        });

        // Hover listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });

        // Custom UI
        setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("▼");
                button.setFont(new Font("Arial", Font.BOLD, 12));
                button.setForeground(Color.WHITE);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return button;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(isHovered ? hoverBackground : backgroundColor);
                g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);

                g2.setColor(isHovered ? hoverBorderColor : borderColor);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 12, 12);

                g2.dispose();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // make background transparent (UI handles drawing)
        setBackground(new Color(0, 0, 0, 0));
    }
}
