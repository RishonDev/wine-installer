package org.javaopensoft;

import javazoom.jl.decoder.JavaLayerException;
import org.javaopensoft.theme.*;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import jadt.utils.Audio.AudioMP3;

/**
 * Wine Installer UI — full file with working welcome animation (shrink -> subtitle fade -> fade out).
 */
public class WINE {

    private static byte function = 0;

    // === Frame & Layout ===
    private static final int FRAME_W = 965;
    private static final int FRAME_H = 540;
    private static final JFrame frame = new JFrame("Wine Installer");
    private static final CardLayout cardLayout = new CardLayout();
    private static final JPanel cards = new JPanel(cardLayout);

    // === Welcome Screen ===
    private static final WPanel welcomePanel = new WPanel();
    private static final FadingLabel welcomeLabel = new FadingLabel("Welcome");
    private static final FadingLabel clickLabel = new FadingLabel("Click anywhere to start");

    // === Home Screen ===
    private static final WPanel homePanel = new WPanel();
    private static final WLabel homeLabel = new WLabel("Choose an option from below");
    private static final WButton installButton = new WButton("Install Wine");
    private static final WButton remove = new WButton("Uninstall Wine");
    private static final WButton revert = new WButton("Go Back to Vanilla Wine");
    private static final WButton installVanilla = new WButton("Install Vanilla Wine");
    private static final WButton buildWineFromSource = new WButton("Build from Source (Advanced users only)");

    // === EULA Screen ===
    private static final WPanel eulaPanel = new WPanel();
    private static final JTextPane eula = new JTextPane();
    private static final JLabel eulaLabel = new JLabel("Software License Agreement:");
    private static final JScrollPane scrollPane = new JScrollPane(eula);
    private static final WCheckBox agree = new WCheckBox("I have read the Terms and Conditions");
    private static final WButton eulaOk = new WButton("Continue");
    private static final WButton eulaBack = new WButton("Back");

    // === Terminal Screen ===
    private static final WPanel terminalPanel = new WPanel();
    private static final Terminal terminal = new Terminal();
    private static final WLabel label = new WLabel("Choose your edition of wine:");
    private static final WCheckBox installConfirm = new WCheckBox("I hereby confirm that I want these specifications when installing wine");
    private static final WButton terminalRun = new WButton("Run");
    private static final WButton terminalBack = new WButton("Back");
    private static final WProgressBar progressBar = new WProgressBar(0, 100);
    // === Uninstall panel ===
    private static final WPanel terminalPanel2 = new WPanel();
    private static final WCheckBox installConfirm2 = new WCheckBox("I hereby confirm that I want these specifications when installing wine");
    private static final WButton terminalRun2 = new WButton("Run");
    private static final WButton terminalBack2 = new WButton("Back");
    private static final WProgressBar progressBar2 = new WProgressBar(0, 100);
    private static final Terminal terminal2 = new Terminal();
    /* ===== Custom fading label class that keeps WLabel behavior but supports alpha transparency ===== */
    static class FadingLabel extends WLabel {

        private float alpha = 1.0f;

        public FadingLabel(String text) {
            super(text);
            setOpaque(false);
        }

        public void setAlpha(float alpha) {
            this.alpha = Math.max(0f, Math.min(1f, alpha));
            repaint();
        }

        public float getAlpha() {
            return alpha;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        // Swing UI on EDT
        SwingUtilities.invokeLater(() -> {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // --- Welcome screen ---
            WPanel welcomePanel = new WPanel();
            welcomePanel.setLayout(null);

            FadingLabel welcomeLabel = new FadingLabel("Welcome");
            FadingLabel clickLabel = new FadingLabel("Click anywhere to start");

            // initial fonts / alpha
            final int START_FONT = 200;
            final int END_FONT = 70;
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, START_FONT));
            welcomeLabel.setAlpha(1f);

            clickLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            clickLabel.setAlpha(0f); // invisible at start

            // Helper to center a label horizontally given its font metrics
            Runnable layoutWelcomeLabel = () -> {
                Font f = welcomeLabel.getFont();
                FontMetrics fm = welcomeLabel.getFontMetrics(f);
                int textW = fm.stringWidth(welcomeLabel.getText());
                int textH = fm.getHeight();
                int x = Math.max(0, (FRAME_W - textW) / 2);
                int y = 120;
                welcomeLabel.setBounds(x, y, Math.max(200, textW + 20), Math.max(40, textH + 6));
                welcomeLabel.revalidate();
                welcomeLabel.repaint();
            };

            Runnable layoutClickLabel = () -> {
                Font f = clickLabel.getFont();
                FontMetrics fm = clickLabel.getFontMetrics(f);
                int textW = fm.stringWidth(clickLabel.getText());
                int textH = fm.getHeight();
                int x = Math.max(0, (FRAME_W - textW) / 2);
                int y = 220;
                clickLabel.setBounds(x, y, Math.max(200, textW + 20), Math.max(30, textH + 4));
                clickLabel.revalidate();
                clickLabel.repaint();
            };

            // initial layout
            layoutWelcomeLabel.run();
            layoutClickLabel.run();

            // add via your custom method so theme/layouting stays consistent
            welcomePanel.addComponent(welcomeLabel);
            welcomePanel.addComponent(clickLabel);

            // Shrink animation: reduce font size stepwise and re-compute bounds so it visibly shrinks
            final int[] currentSize = {START_FONT};
            Timer shrinkTimer = new Timer(30, null);
            shrinkTimer.addActionListener(e -> {
                if (currentSize[0] > END_FONT) {
                    currentSize[0] = currentSize[0] - 6; // step
                    if (currentSize[0] < END_FONT) currentSize[0] = END_FONT;
                    welcomeLabel.setFont(new Font("Arial", Font.BOLD, currentSize[0]));
                    layoutWelcomeLabel.run();
                } else {
                    ((Timer) e.getSource()).stop();
                    // Fade in clickLabel
                    Timer fadeIn = new Timer(35, null);
                    fadeIn.addActionListener(ev -> {
                        float a = clickLabel.getAlpha() + 0.06f;
                        if (a >= 1f) {
                            clickLabel.setAlpha(1f);
                            ((Timer) ev.getSource()).stop();
                        } else {
                            clickLabel.setAlpha(a);
                        }
                    });
                    fadeIn.start();
                }
            });
            shrinkTimer.setInitialDelay(200);
            shrinkTimer.start();

            // Click anywhere: fade out both labels then switch to home screen
            MouseAdapter startClickHandler = new MouseAdapter() {
                private boolean clicked = false;

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (clicked) return;
                    clicked = true;

                    Timer fadeOut = new Timer(35, null);
                    fadeOut.addActionListener(ev -> {
                        float a1 = welcomeLabel.getAlpha() - 0.06f;
                        float a2 = clickLabel.getAlpha() - 0.06f;
                        welcomeLabel.setAlpha(Math.max(0f, a1));
                        clickLabel.setAlpha(Math.max(0f, a2));
                        if (a1 <= 0f && a2 <= 0f) {
                            ((Timer) ev.getSource()).stop();
                            cardLayout.show(cards, "home");
                        }
                    });
                    fadeOut.start();
                }
            };

            welcomePanel.addMouseListener(startClickHandler);
            welcomeLabel.addMouseListener(startClickHandler);
            clickLabel.addMouseListener(startClickHandler);

            // --- Home screen ---
            homePanel.setLayout(null);
            homeLabel.setFont(new Font("Arial", Font.BOLD, 20));
            homeLabel.setForeground(Color.WHITE);
            homeLabel.setBounds(20, 10, 600, 30);
            homePanel.addComponent(homeLabel);

            installButton.setBounds(350, 60, 300, 50);
            remove.setBounds(350, 120, 300, 50);
            revert.setBounds(350, 180, 300, 50);
            installVanilla.setBounds(350, 240, 300, 50);
            buildWineFromSource.setBounds(300, 300, 400, 50);

            homePanel.addComponent(installButton);
            homePanel.addComponent(remove);
            homePanel.addComponent(revert);
            homePanel.addComponent(installVanilla);
            homePanel.addComponent(buildWineFromSource);

            // --- EULA screen setup (kept robust) ---
            for (String str : getEULA()) {
                eula.setText(eula.getText().concat(str));
            }
            eulaLabel.setForeground(Color.WHITE);
            eulaLabel.setFont(new Font("Arial", Font.BOLD, 18));
            eula.setEditable(false);
            eula.setBorder(null);
            eula.setFont(new Font("Serif", Font.PLAIN, 18));

            eulaLabel.setBounds(20, 10, 600, 25);
            scrollPane.setBounds(20, 40, 900, 345);
            agree.setBounds(20, 405, 300, 30);
            eulaOk.setBounds(700, 455, 120, 40);
            eulaBack.setBounds(20, 455, 120, 40);
            eulaOk.setEnabled(false);

            eulaPanel.addComponent(eulaLabel);
            eulaPanel.addComponent(eulaOk);
            eulaPanel.addComponent(eulaBack);
            eulaPanel.addComponent(scrollPane);
            eulaPanel.addComponent(agree);
            //eula.setContentType("text/html");
            StyledDocument doc = eula.getStyledDocument();
            SimpleAttributeSet center = new SimpleAttributeSet();
            StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
            // Guard: if doc length is 0 this call is safe
            doc.setParagraphAttributes(0, doc.getLength() > 0 ? doc.getLength() : 1, center, false);

            agree.addActionListener(e -> eulaOk.setEnabled(agree.isSelected()));
            eulaBack.addActionListener(e -> cardLayout.show(cards, "home"));

            // --- Terminal screen (kept original layout) ---
            String[] versions = terminal.GetOutput("base.sh --list");
            String[] editions = new String[versions.length+3];
            editions[0] = "Stable (Reliable)";
            editions[1] = "Staging (Balanced)";
            editions[2]= "Development (Newest features, may be buggy)";
            for(int i =0; i < versions.length; i++) {
                if(!versions[i].equals("Could not detect distribution")){
                    editions[i + 3] = versions[i];
                }
            }
            WComboBox<String> editionBox = new WComboBox<>(editions);
            //WProgressBar progressBar = new WProgressBar(0, 100);

            terminal.setBounds(80, 100, 300, 800);
            terminalRun.setBounds(780, 470, 100, 30);
            terminalRun.setEnabled(false);
            terminalBack.setBounds(70, 470, 100, 30);
            progressBar.setBounds(80, 410, 800, 20);
            editionBox.setBounds(320, 50, 360, 30);
            label.setBounds(100, 50, 300, 30);
            installConfirm.setBounds(170, 430, 700, 30);
            installConfirm2.setBounds(170, 430, 700, 30);
            terminal2.setBounds(80, 100, 300, 800);
            terminalRun2.setBounds(780, 470, 100, 30);
            terminalRun2.setEnabled(false);
            terminalBack2.setBounds(70, 470, 100, 30);
            progressBar2.setBounds(80, 410, 800, 20);
            terminalPanel.addComponent(editionBox);
            terminalPanel.addComponent(terminalRun);
            terminalPanel.addComponent(terminalBack);
            terminalPanel.addComponent(label);
            terminalPanel.addComponent(installConfirm);
            terminalPanel.addComponent(progressBar);
            terminalPanel.addComponent(terminal.getScrollPane());
            terminalPanel2.addComponent(terminalRun2);
            terminalPanel2.addComponent(terminalBack2);
            terminalPanel2.addComponent(installConfirm2);
            terminalPanel2.addComponent(progressBar2);
            terminalPanel2.addComponent(terminal2.getScrollPane());

            installConfirm.addActionListener(e -> terminalRun.setEnabled(installConfirm.isSelected()));

            // --- Cards ---
            cards.add(welcomePanel, "welcome");
            cards.add(homePanel, "home");
            cards.add(eulaPanel, "eula");
            cards.add(terminalPanel, "terminal");
            cards.add(terminalPanel2, "terminal2");

            // Taskbar icon (best-effort)
            try {
                Taskbar taskbar = Taskbar.getTaskbar();
                taskbar.setIconImage(Toolkit.getDefaultToolkit().getImage(
                        WINE.class.getResource("winelogo.png")));
            } catch (Throwable ignored) { }

            // Button actions (install flow)
            installButton.addActionListener(e -> {
                cardLayout.show(cards, "eula");
                function = 1;
            });

            remove.addActionListener(e -> {
                function = 2;
                cardLayout.show(cards, "terminal2");
            });
            terminalRun.addActionListener(e -> {
                String ver = switch (editionBox.getSelectedIndex()) {
                    case 0 -> "stable";
                    case 1 -> "staging";
                    //case 2 -> "dev";
                    default -> String.valueOf(editionBox.getSelectedItem());
                };
                if (ver != null) {
                    switch (function) {
                        case 1 -> {
                            terminal.runScript("base.sh --install " + ver);
                        }


                        case 2 -> {
                            System.out.println(editionBox.isVisible());
                            terminalPanel.revalidate();
                            terminalPanel.repaint();
                            byte option = (byte) WOptionPane.showConfirmDialog(terminalPanel, "Are you sure that you want to uninstall?", "Uninstall");
                            switch (option) {
                                case -1 -> {
                                    while (option == -1)
                                        option = (byte) WOptionPane.showConfirmDialog(terminalPanel, "Are you sure that you want to uninstall?", "Uninstall");
                                }
                                case 0 -> terminal.runCommand("base.sh --uninstall");
                                case 1 -> {
                                    function = 0;
                                    cardLayout.show(cards, "home");
                                }
                            }
                        }
                        case 3 -> {

                        }
                }
            }
        });
            eulaOk.addActionListener(e -> cardLayout.show(cards, "terminal"));
            terminalBack.addActionListener(e ->{
                    switch (backFunction) {
                        case 1 -> cardLayout.show(cards, "eula");
                        case 2 -> cardLayout.show(cards, "home");
                    }
                }
            );

            // --- Frame setup ---
            frame.setResizable(false);
            frame.setContentPane(cards);
            frame.setSize(FRAME_W, FRAME_H);
            frame.setVisible(true);

            // play startup audio if present (best-effort)
            try {
                var startup = new AudioMP3("src/main/resources/org/javaopensoft/startup.mp3");
                startup.play();
            } catch (FileNotFoundException | JavaLayerException ex) {
                System.err.println("Startup audio not found or failed to play: " + ex.getMessage());
            } catch (Throwable ignored) { }

            // Start on Welcome screen
            cardLayout.show(cards, "welcome");
        });
    }

    /**
     * Reads the packaged LICENSE.txt (EULA) and returns the lines.
     * If resource is missing, returns a short friendly message.
     */
    public static String[] getEULA() {
        String[] lines = new String[516];
        InputStream is = WINE.class.getResourceAsStream("/org/javaopensoft/LICENSE.txt");
        if (is == null) {
            System.err.println("""
                    EULA file not found in resources.
                    Make sure /org/javaopensoft/LICENSE.txt is packaged inside the JAR
                    """);
            System.exit(1);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            for(int i = 0; i< lines.length; i++){
                    lines[i] = reader.readLine() + "\n";
            }
        } catch (IOException e) {
            System.err.println("Error reading EULA: " + e.getMessage());
        }
        return lines;
    }

    /**
     * Extracts a script resource to a temp file, makes it executable, and returns its path.
     */
    public static String extractScript(String resourceName) throws IOException {
        File tempScript = File.createTempFile("wine-installer-", ".sh");
        tempScript.deleteOnExit();

        try (InputStream in = WINE.class.getResourceAsStream("/shell/" + resourceName)) {
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + resourceName);
            }
            try (OutputStream out = new FileOutputStream(tempScript)) {
                in.transferTo(out);
            }
        }

        // Make it executable
        if (!tempScript.setExecutable(true)) {
            System.err.println("Warning: failed to mark temp script executable (platform may not allow it).");
        }

        return tempScript.getAbsolutePath();
    }
}
