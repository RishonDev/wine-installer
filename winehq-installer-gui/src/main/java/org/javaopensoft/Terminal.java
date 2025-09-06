package org.javaopensoft;

import lombok.Getter;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
public class Terminal{
    private final JTextArea textArea = new JTextArea();
    @Getter
    JScrollPane scrollPane = new JScrollPane(textArea);
    public Terminal() {
        textArea.setEditable(false);
    }
    public Terminal(String command) {
        textArea.setEditable(false);
        // Run command in background thread
        new Thread(() -> runCommand(command)).start();
    }

    private void runCommand(String command) {
        textArea.append("$ ".concat(command).concat("\n"));
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
