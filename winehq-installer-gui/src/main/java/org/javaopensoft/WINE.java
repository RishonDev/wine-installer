package org.javaopensoft;

import org.javaopensoft.theme.WButton;
import org.javaopensoft.theme.WCheckBox;
import org.javaopensoft.theme.WPanel;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WINE {
    static JFrame frame = new JFrame("Wine Installer");
    static CardLayout cardLayout = new CardLayout();
    static JPanel cards = new JPanel(cardLayout);

    //eula
    static JTextPane eula = new JTextPane();
    static JLabel eula_lbl1 = new JLabel("Software License Agreement:");
    static WButton eula_ok = new WButton("Continue");
    static WButton eula_back = new WButton("Back");
    static JScrollPane scrollPane = new JScrollPane(eula);
    static WPanel eulaPanel = new WPanel();
    static WCheckBox agree = new WCheckBox("I have read the Terms and Conditions");
    public static void main(String[] args) {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Panels (your custom Panel class)
        WPanel homePanel = new WPanel();
        for(String str : getEULA())eula.setText(eula.getText().concat(str));
        WPanel terminalPanel = new WPanel();
        // --- Home screen ---
        WButton button = new WButton();
        button.setText("Install Wine");
        button.setBounds(170, 250, 300, 50);
        homePanel.addComponent(button); // add button to background

        // --- EULA screen ---
        eula_lbl1.setForeground(Color.WHITE);      // high contrast
        eula_lbl1.setFont(new Font("Arial", Font.BOLD, 18));
        eula.setEditable(false);
        eula.setBorder(null);
        eula_lbl1.setBounds(20, 10, 600, 25);
        scrollPane.setBounds(20, 40, 900, 345);
        agree.setBounds(20, 405, 300, 30);
        eula_ok.setBounds(700, 455, 120, 40);
        eula_back.setBounds(20, 455, 120, 40);
        eula_ok.setEnabled(false);
        eulaPanel.addComponent(eula_lbl1);
        eulaPanel.addComponent(eula_ok);
        eulaPanel.addComponent(eula_back);
        eulaPanel.addComponent(scrollPane);
        eulaPanel.addComponent(agree);
        eula.setFont(new Font("Serif", Font.PLAIN, 18));
        StyledDocument doc = eula.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        agree.setVisible(true);
        agree.addActionListener(e ->{
            eula_ok.setEnabled(agree.isSelected());
        });
        eula_back.addActionListener(e
                -> cardLayout.show(cards, "home"));

        // --- Terminal screen ---
        Terminal terminal = new Terminal();
        WButton run = new WButton("Run");
        String[] ed = {"stable", "staging","devel"};
        JComboBox<String> edition = new JComboBox<>(ed);
        JProgressBar progressBar = new JProgressBar();
        terminalPanel.addComponent(terminal.getScrollPane());

        // Add panels to cards
        cards.add(homePanel, "home");
        cards.add(eulaPanel, "eula");
        cards.add(terminalPanel, "terminal");
        Taskbar taskbar = Taskbar.getTaskbar();
        taskbar.setIconImage(Toolkit.getDefaultToolkit().getImage(WINE.class.getResource("winelogo.png")));
        // Show first card
        frame.setResizable(false);
        frame.setContentPane(cards);
        frame.setSize(965, 540);
        frame.setVisible(true);

        // Button action → switch to EULA
        button.addActionListener(e -> cardLayout.show(cards, "eula"));
        eula_ok.addActionListener(e -> cardLayout.show(cards, "terminal"));
        cardLayout.show(cards, "eula");

    }

    public static String[] getEULA() {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        WINE.class.getResourceAsStream("/org/javaopensoft/LICENSE.txt"), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null)
                lines.add(line + "\n");

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines.toArray(new String[0]);
    }
}
