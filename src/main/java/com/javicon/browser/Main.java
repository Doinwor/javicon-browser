package com.javicon.browser;

import javafx.application.Platform;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Не удалось установить системный Look-and-Feel: " + e.getMessage());
        }

        Platform.startup(() -> {
            // JavaFX toolkit initialized; UI built in the next step
        });

        SwingUtilities.invokeLater(() -> {
            BrowserWindow window = new BrowserWindow();
            window.setVisible(true);
        });
    }
}
