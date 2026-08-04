package com.javicon.browser;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Image;
import java.util.List;

public class FavoritesWindow extends JDialog {

    private final BrowserWindow parent;
    private final BookmarkManager bookmarkManager;
    private final HistoryManager historyManager;

    private DefaultListModel<BookmarkManager.Bookmark> bookmarkModel = new DefaultListModel<>();
    private DefaultListModel<String> historyModel = new DefaultListModel<>();
    private JList<BookmarkManager.Bookmark> bookmarkList;
    private JList<String> historyList;

    public FavoritesWindow(BrowserWindow parent,
                           BookmarkManager bookmarkManager,
                           HistoryManager historyManager) {
        super(parent, "Закладки и история", true);
        this.parent = parent;
        this.bookmarkManager = bookmarkManager;
        this.historyManager = historyManager;
        setSize(520, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        initUI();
        refresh();
    }

    private ImageIcon loadBanner() {
        try {
            return new ImageIcon(getClass().getResource("/banner.png"));
        } catch (Exception e) {
            System.err.println("Не удалось загрузить баннер: " + e.getMessage());
            return null;
        }
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Закладки", createBookmarksPanel());
        tabs.addTab("История", createHistoryPanel());

        JPanel topPanel = new JPanel(new BorderLayout());
        ImageIcon banner = loadBanner();
        if (banner != null) {
            int w = Math.min(banner.getIconWidth(), 180);
            Image scaled = banner.getImage().getScaledInstance(w, -1, Image.SCALE_SMOOTH);
            JLabel bannerLabel = new JLabel(new ImageIcon(scaled));
            topPanel.add(bannerLabel, BorderLayout.EAST);
        }
        topPanel.add(tabs, BorderLayout.CENTER);
        add(topPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Закрыть");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createBookmarksPanel() {
        bookmarkList = new JList<>(bookmarkModel);
        bookmarkList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton openBtn = new JButton("Открыть");
        openBtn.addActionListener(e -> openSelectedBookmark());

        JButton deleteBtn = new JButton("Удалить");
        deleteBtn.addActionListener(e -> deleteSelectedBookmark());

        JButton exportBtn = new JButton("Экспорт");
        exportBtn.addActionListener(e -> parent.exportBookmarks());

        JButton importBtn = new JButton("Импорт");
        importBtn.addActionListener(e -> parent.importBookmarks());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(importBtn);

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(bookmarkList), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createHistoryPanel() {
        historyList = new JList<>(historyModel);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton openBtn = new JButton("Открыть");
        openBtn.addActionListener(e -> openSelectedHistory());

        JButton clearBtn = new JButton("Очистить историю");
        clearBtn.addActionListener(e -> clearHistory());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openBtn);
        buttonPanel.add(clearBtn);

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JScrollPane(historyList), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refresh() {
        bookmarkModel.clear();
        for (BookmarkManager.Bookmark b : bookmarkManager.getBookmarks()) {
            bookmarkModel.addElement(b);
        }
        historyModel.clear();
        for (String url : parent.getHistoryUrls()) {
            historyModel.addElement(url);
        }
    }

    private void openSelectedBookmark() {
        BookmarkManager.Bookmark b = bookmarkList.getSelectedValue();
        if (b != null) {
            parent.navigateTo(b.url());
            dispose();
        }
    }

    private void deleteSelectedBookmark() {
        int idx = bookmarkList.getSelectedIndex();
        if (idx >= 0) {
            bookmarkManager.removeBookmark(idx);
            refresh();
        }
    }

    private void openSelectedHistory() {
        String url = historyList.getSelectedValue();
        if (url != null) {
            parent.navigateTo(url);
            dispose();
        }
    }

    private void clearHistory() {
        if (JOptionPane.showConfirmDialog(this, "Удалить всю историю посещений?",
                "Очистка истории", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            parent.clearHistory();
            refresh();
        }
    }
}
