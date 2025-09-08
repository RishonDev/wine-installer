package org.javaopensoft;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Terminal {
    private final JTextArea textArea = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(textArea);

    public Terminal() {
        textArea.setEditable(false);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setBounds(int x, int y, int l, int b) {
        scrollPane.setBounds(x, y, b, l);
    }

    public Terminal(String command) {
        textArea.setEditable(false);
        new Thread(() -> runCommand(command)).start();
    }

    public void runCommand(String command) {
        textArea.append("$ " + command + "\n");
        try {
            ProcessBuilder builder = new ProcessBuilder();
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                builder.command("cmd.exe", "/c", command);
            } else {
                builder.command("bash", "-c", command);
            }

            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final String output = line;
                    SwingUtilities.invokeLater(() -> {
                        textArea.append(output + "\n");
                        textArea.setCaretPosition(textArea.getDocument().getLength());
                    });
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
