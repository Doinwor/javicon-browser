package com.javicon.browser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {

    private static final File HISTORY_FILE =
            new File(System.getProperty("user.home") + "/.javicon/history.txt");

    public record HistoryData(List<String> urls, int index) {
    }

    public HistoryData loadHistory() {
        if (!HISTORY_FILE.exists()) {
            return new HistoryData(new ArrayList<>(), -1);
        }
        try {
            List<String> urls = new ArrayList<>();
            int index = -1;
            List<String> lines = Files.readAllLines(HISTORY_FILE.toPath(), StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.equals("<index>")) {
                    index = urls.size() - 1;
                } else {
                    urls.add(line);
                }
            }
            return new HistoryData(urls, index);
        } catch (IOException e) {
            System.err.println("Не удалось загрузить историю: " + e.getMessage());
            return new HistoryData(new ArrayList<>(), -1);
        }
    }

    public void saveHistory(List<String> history, int currentIndex) {
        File dir = HISTORY_FILE.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        List<String> lines = new ArrayList<>(history);
        if (currentIndex >= 0 && currentIndex < lines.size()) {
            lines.add(currentIndex, "<index>");
        }
        try {
            Files.write(HISTORY_FILE.toPath(), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Не удалось сохранить историю: " + e.getMessage());
        }
    }
}
