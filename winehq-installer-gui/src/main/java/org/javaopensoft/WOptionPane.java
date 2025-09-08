package org.javaopensoft;

import org.javaopensoft.theme.WButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WOptionPane {

    private static Image bgImage;

    static {
        try {
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            bgImage = Toolkit.getDefaultToolkit().getImage(WOptionPane.class.getResource("bg2.png"));
        } catch (IllegalArgumentException e) {
            System.err.println("Background image not found: " + e.getMessage());
            bgImage = null;
        }
    }

    // Message dialog
    public static void showMessageDialog(Component parent, String message, String title) {
        showCustomDialog(parent, message, title, new String[]{"OK"}, 0);
    }

    // Error dialog
    public static void showErrorDialog(Component parent, String message, String title) {
        showCustomDialog(parent, message, title, new String[]{"OK"}, 0);
    }

    // Confirm dialog
    public static int showConfirmDialog(Component parent, String message, String title) {
        return showCustomDialog(parent, message, title, new String[]{"Yes", "No"}, 0);
    }

    private static int showCustomDialog(Component parent, String message, String title, String[] options, int defaultOption) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true); // necessary for rounded corners
        dialog.setAlwaysOnTop(true);

        // Rounded panel with background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        // --- Custom Title Bar (no close button) ---
        JPanel titleBar = new JPanel();
        titleBar.setLayout(new BorderLayout());
        titleBar.setPreferredSize(new Dimension(0, 30));
        titleBar.setBackground(new Color(0, 0, 0, 120)); // semi-transparent dark

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        titleBar.add(titleLabel, BorderLayout.WEST);

        // Make dialog draggable
        final Point[] mouseDownCompCoords = {null};
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords[0] = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                dialog.setLocation(currCoords.x - mouseDownCompCoords[0].x, currCoords.y - mouseDownCompCoords[0].y);
            }
        });

        panel.add(titleBar, BorderLayout.NORTH);

        // Message label
        JLabel label = new JLabel("<html><body style='text-align:center;'>" + message + "</body></html>");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.add(label, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        final int[] result = {-1};
        for (int i = 0; i < options.length; i++) {
            WButton btn = new WButton(options[i]);
            final int optionIndex = i;
            btn.addActionListener(e -> {
                result[0] = optionIndex;
                dialog.dispose();
            });
            buttonsPanel.add(btn);
        }
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.pack(); // automatic sizing

        // Rounded corners
        dialog.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0,
                dialog.getWidth(), dialog.getHeight(), 25, 25));

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        return result[0];
    }
}
