package com.javicon.browser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BookmarkManager {

    public record Bookmark(String title, String url) {
        @Override
        public String toString() {
            return title;
        }
    }

    private static final File BOOKMARKS_FILE =
            new File(System.getProperty("user.home") + "/.javicon/bookmarks.txt");

    private final List<Bookmark> bookmarks = new ArrayList<>();

    public BookmarkManager() {
        load();
    }

    private void load() {
        if (!BOOKMARKS_FILE.exists()) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(BOOKMARKS_FILE.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                int tab = line.indexOf('\t');
                if (tab < 0) {
                    continue;
                }
                String title = line.substring(0, tab).trim();
                String url = line.substring(tab + 1).trim();
                if (!url.isEmpty()) {
                    bookmarks.add(new Bookmark(title.isEmpty() ? url : title, url));
                }
            }
        } catch (IOException e) {
            System.err.println("Не удалось загрузить закладки: " + e.getMessage());
        }
    }

    public void addBookmark(String title, String url) {
        for (Bookmark b : bookmarks) {
            if (b.url().equals(url)) {
                return;
            }
        }
        bookmarks.add(new Bookmark(title.isEmpty() ? url : title, url));
        save();
    }

    public void removeBookmark(int index) {
        if (index >= 0 && index < bookmarks.size()) {
            bookmarks.remove(index);
            save();
        }
    }

    public List<Bookmark> getBookmarks() {
        return new ArrayList<>(bookmarks);
    }

    private void save() {
        File dir = BOOKMARKS_FILE.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        List<String> lines = new ArrayList<>();
        for (Bookmark b : bookmarks) {
            lines.add(b.title() + "\t" + b.url());
        }
        try {
            Files.write(BOOKMARKS_FILE.toPath(), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Не удалось сохранить закладки: " + e.getMessage());
        }
    }
}
