package org.javaopensoft;

import org.javaopensoft.theme.*;

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
    static byte function = 0;
    //Home
    static JFrame frame = new JFrame("Wine Installer");
    static CardLayout cardLayout = new CardLayout();
    static JPanel cards = new JPanel(cardLayout);
    static WButton remove = new WButton("Uninstall Wine");
    static WButton revert = new WButton("Go Back to Vanilla Wine");
    static WButton install_vanilla = new WButton("Install Vanilla Wine");
    ArrayList<String> commands = new ArrayList<>();

    // EULA
    static JTextPane eula = new JTextPane();
    static JLabel eula_lbl1 = new JLabel("Software License Agreement:");
    static WButton eula_ok = new WButton("Continue");
    static WButton eula_back = new WButton("Back");
    static JScrollPane scrollPane = new JScrollPane(eula);
    static WPanel eulaPanel = new WPanel();
    static WCheckBox agree = new WCheckBox("I have read the Terms and Conditions");


    public static void main(String[] args) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- Home screen ---
        WPanel homePanel = new WPanel();
        WButton install_button = new WButton("Install Wine");
        install_button.setBounds(170, 250, 300, 50);
        homePanel.addComponent(install_button);

        // --- EULA screen ---
        for (String str : getEULA()) eula.setText(eula.getText().concat(str));
        eula_lbl1.setForeground(Color.WHITE);
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

        agree.addActionListener(e -> eula_ok.setEnabled(agree.isSelected()));
        eula_back.addActionListener(e -> cardLayout.show(cards, "home"));

        // --- Terminal screen ---
        WPanel terminalPanel = new WPanel();
        Terminal terminal = new Terminal();
        WLabel label = new WLabel("Choose your edition of wine:");
        WCheckBox install_confirm = new WCheckBox("I hereby confirm that I want these specifications when installing of wine");
        WButton terminal_run = new WButton("Run");
        WButton terminal_back = new WButton("Back");
        String[] ed = {"Stable (Reliable)", "Staging (Balanced)", "Development (Newest features, may be buggy)"};
        WComboBox<String> edition = new WComboBox<>(ed);
        WProgressBar progressBar = new WProgressBar(0,100);
        // (no bounds assigned here in original)
        terminal.setBounds(80, 100, 300, 800);
        terminal_run.setBounds(780,470, 100,30);
        terminal_run.setEnabled(false);
        terminal_back.setBounds(70,470, 100,30);
        progressBar.setBounds(80,410,800,20);
        edition.setBounds(320,50,360,30);
        label.setBounds(100,50,300,30);
        install_confirm.setBounds(170,430, 700,30);
        terminalPanel.addComponent(edition);
        terminalPanel.addComponent(terminal_run);
        terminalPanel.addComponent(terminal_back);
        terminalPanel.addComponent(label);
        terminalPanel.addComponent(install_confirm);
        terminalPanel.addComponent(progressBar);
        terminalPanel.addComponent(terminal.getScrollPane());

        install_confirm.addActionListener(e ->{
            terminal_run.setEnabled(install_confirm.isSelected());
        });
        // Add panels to cards
        cards.add(homePanel, "home");
        cards.add(eulaPanel, "eula");
        cards.add(terminalPanel, "terminal");

        try {
            Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(Toolkit.getDefaultToolkit().getImage(
                    WINE.class.getResource("winelogo.png")));
        } catch (UnsupportedOperationException ignored) {}


        // Button actions
        install_button.addActionListener(e -> {
            cardLayout.show(cards, "eula");
            function=1;
        });
        terminal_run.addActionListener(e ->{
            switch(function){
                case 1->{
                    terminal.runCommand("./src/main/");
                }
                case 2->{}
                case 3->{}
                case 4->{}
                case 5->{}
                case 6->{}
            }
        });
        eula_ok.addActionListener(e -> cardLayout.show(cards, "terminal"));
        terminal_back.addActionListener(e -> cardLayout.show(cards, "eula"));
        // Show first card
        frame.setResizable(false);
        frame.setContentPane(cards);
        frame.setSize(965, 540);
        frame.setVisible(true);
        cardLayout.show(cards, "terminal");
    }

    public static String[] getEULA() {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        WINE.class.getResourceAsStream("/org/javaopensoft/LICENSE.txt"),
                        StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null)
                lines.add(line + "\n");

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines.toArray(new String[0]);
    }
}
