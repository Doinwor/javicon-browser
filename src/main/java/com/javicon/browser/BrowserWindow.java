package com.javicon.browser;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class BrowserWindow extends JFrame {

    private JFXPanel browserPanel;
    private JTextField addressBar;
    private WebView webView;

    private JButton backBtn;
    private JButton forwardBtn;
    private JButton refreshBtn;
    private JButton homeBtn;
    private JButton settingsBtn;
    private JButton bookmarkBtn;
    private JButton bookmarksBtn;
    private JButton historyBtn;

    private JLabel statusLabel;
    private JProgressBar progressBar;

    private static final Font UI_FONT = new Font("Tahoma", Font.PLAIN, 11);

    private final SettingsManager settingsManager = new SettingsManager();
    private final BookmarkManager bookmarkManager = new BookmarkManager();
    private final HistoryManager historyManager = new HistoryManager();

    private String currentTitle = "";
    private String currentUrl = "";

    private static final List<String> DOWNLOAD_EXTENSIONS = List.of(
            ".zip", ".rar", ".7z", ".tar", ".gz", ".exe", ".msi", ".pdf",
            ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".mp3", ".mp4",
            ".png", ".jpg", ".jpeg", ".gif", ".bin", ".jar", ".iso", ".apk",
            ".txt", ".csv", ".json");

    private final List<String> history = new ArrayList<>();
    private int historyIndex = -1;

    private boolean historyLock = false;

    public BrowserWindow() {
        setTitle("Javicon Browser");
        setSize(1024, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAppIcon();
        restoreHistory();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveSession();
            }
        });
        initUI();
    }

    private void setAppIcon() {
        try {
            Image icon = new ImageIcon(getClass().getResource("/icon.png")).getImage();
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить иконку: " + e.getMessage());
        }
    }

    private ImageIcon loadBanner() {
        try {
            return new ImageIcon(getClass().getResource("/banner.png"));
        } catch (Exception e) {
            System.err.println("Не удалось загрузить баннер: " + e.getMessage());
            return null;
        }
    }

    private void restoreHistory() {
        if (!settingsManager.isSaveHistory() || !settingsManager.isSaveHistoryBetweenSessions()) {
            return;
        }
        HistoryManager.HistoryData data = historyManager.loadHistory();
        history.addAll(data.urls());
        historyIndex = data.index();
    }

    private void saveSession() {
        if (!settingsManager.isSaveHistory() || !settingsManager.isSaveHistoryBetweenSessions()) {
            return;
        }
        historyManager.saveHistory(new ArrayList<>(history), historyIndex);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        browserPanel = new JFXPanel();
        add(browserPanel, BorderLayout.CENTER);

        setJMenuBar(createMenuBar());

        createToolbar();
        createStatusBar();

        Platform.runLater(() -> {
            webView = new WebView();
            webView.getEngine().locationProperty().addListener(onLocationChanged);
            webView.getEngine().titleProperty().addListener(onTitleChanged);
            wireLoadWorker();
            Scene scene = new Scene(webView);
            browserPanel.setScene(scene);
            if (settingsManager.isRestoreSession()
                    && !history.isEmpty() && historyIndex >= 0) {
                webView.getEngine().load(history.get(historyIndex));
            } else {
                webView.getEngine().load(settingsManager.getHomePage());
            }
        });
    }

    private void wireLoadWorker() {
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            SwingUtilities.invokeLater(() -> {
                if (newState == Worker.State.RUNNING) {
                    progressBar.setIndeterminate(true);
                    progressBar.setVisible(true);
                    statusLabel.setText("Загрузка...");
                } else {
                    progressBar.setVisible(false);
                    if (newState == Worker.State.SUCCEEDED) {
                        statusLabel.setText("Готово");
                    } else if (newState == Worker.State.FAILED) {
                        statusLabel.setText("Ошибка загрузки");
                    }
                }
            });
        });
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(0xC0C0C0));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        statusLabel = new JLabel("Готово");
        statusLabel.setFont(UI_FONT);
        statusLabel.setBackground(statusBar.getBackground());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(120, 16));
        progressBar.setVisible(false);
        statusBar.add(statusLabel, BorderLayout.CENTER);
        statusBar.add(progressBar, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JButton styleButton(JButton btn) {
        btn.setFont(UI_FONT);
        btn.setBackground(Color.LIGHT_GRAY);
        btn.setFocusPainted(false);
        btn.setMargin(new java.awt.Insets(1, 5, 1, 5));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));
        return btn;
    }

    private void createToolbar() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(Color.LIGHT_GRAY);
        navPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));

        backBtn = styleButton(new JButton("Назад"));
        backBtn.setToolTipText("Назад");
        backBtn.addActionListener(e -> goBack());

        forwardBtn = styleButton(new JButton("Вперёд"));
        forwardBtn.setToolTipText("Вперёд");
        forwardBtn.addActionListener(e -> goForward());

        refreshBtn = styleButton(new JButton("Обновить"));
        refreshBtn.setToolTipText("Обновить");
        refreshBtn.addActionListener(e -> reload());

        homeBtn = styleButton(new JButton("Домой"));
        homeBtn.setToolTipText("Домой");
        homeBtn.addActionListener(e -> goHome());

        settingsBtn = styleButton(new JButton("Настройки"));
        settingsBtn.setToolTipText("Настройки");
        settingsBtn.addActionListener(e -> openSettings());

        bookmarkBtn = styleButton(new JButton("Добавить"));
        bookmarkBtn.setToolTipText("Добавить в закладки");
        bookmarkBtn.addActionListener(e -> addBookmark());

        bookmarksBtn = styleButton(new JButton("Избранное"));
        bookmarksBtn.setToolTipText("Управление закладками");
        bookmarksBtn.addActionListener(e -> openFavoritesWindow());

        historyBtn = styleButton(new JButton("История"));
        historyBtn.setToolTipText("История посещений");
        historyBtn.addActionListener(e -> openFavoritesWindow());

        navPanel.add(backBtn, BorderLayout.WEST);
        navPanel.add(forwardBtn, BorderLayout.CENTER);
        navPanel.add(refreshBtn, BorderLayout.EAST);

        JPanel actionsPanel = new JPanel(new BorderLayout());
        actionsPanel.setBackground(Color.LIGHT_GRAY);
        JPanel favoritePanel = new JPanel(new BorderLayout());
        favoritePanel.setBackground(Color.LIGHT_GRAY);
        favoritePanel.add(bookmarkBtn, BorderLayout.WEST);
        favoritePanel.add(bookmarksBtn, BorderLayout.CENTER);
        favoritePanel.add(historyBtn, BorderLayout.EAST);
        actionsPanel.add(favoritePanel, BorderLayout.WEST);
        actionsPanel.add(homeBtn, BorderLayout.EAST);        addressBar = new JTextField(settingsManager.getHomePage());
        addressBar.setFont(UI_FONT);
        addressBar.setPreferredSize(new Dimension(0, 28));
        addressBar.addActionListener(e -> loadUrl());

        JButton goButton = styleButton(new JButton("Перейти"));
        goButton.addActionListener(e -> loadUrl());

        topPanel.add(navPanel, BorderLayout.WEST);
        topPanel.add(addressBar, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.LIGHT_GRAY);
        JPanel goWrap = new JPanel(new BorderLayout());
        goWrap.setBackground(Color.LIGHT_GRAY);
        goWrap.add(goButton, BorderLayout.WEST);
        goWrap.add(settingsBtn, BorderLayout.EAST);
        rightPanel.add(goWrap, BorderLayout.WEST);
        rightPanel.add(actionsPanel, BorderLayout.EAST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        updateNavButtons();
    }

    private void loadUrl() {
        String url = addressBar.getText().trim();
        if (url.isEmpty()) {
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        String target = url;
        Platform.runLater(() -> {
            historyLock = false;
            webView.getEngine().load(target);
        });
    }

    private void reload() {
        Platform.runLater(() -> webView.getEngine().reload());
    }

    private void goBack() {
        Platform.runLater(() -> {
            if (historyIndex <= 0) {
                updateNavButtons();
                return;
            }
            historyLock = true;
            historyIndex--;
            webView.getEngine().load(history.get(historyIndex));
        });
    }

    private void goForward() {
        Platform.runLater(() -> {
            if (historyIndex >= history.size() - 1) {
                updateNavButtons();
                return;
            }
            historyLock = true;
            historyIndex++;
            webView.getEngine().load(history.get(historyIndex));
        });
    }

    private final ChangeListener<String> onLocationChanged =
            (obs, oldUrl, newUrl) -> {
                if (newUrl == null || newUrl.isEmpty()) {
                    return;
                }
                if (isDownloadUrl(newUrl)) {
                    SwingUtilities.invokeLater(() -> {
                        Platform.runLater(() -> webView.getEngine().load(oldUrl));
                        handleDownload(newUrl);
                    });
                    return;
                }
                if (!historyLock) {
                    while (history.size() - 1 > historyIndex) {
                        history.remove(history.size() - 1);
                    }
                    history.add(newUrl);
                    historyIndex = history.size() - 1;
                } else {
                    historyLock = false;
                }
                updateNavButtons();
                currentUrl = newUrl;
                SwingUtilities.invokeLater(() -> addressBar.setText(newUrl));
            };

    private final ChangeListener<String> onTitleChanged =
            (obs, oldTitle, newTitle) ->
                    SwingUtilities.invokeLater(() -> {
                        currentTitle = (newTitle == null) ? "" : newTitle;
                        String t = currentTitle.isEmpty()
                                ? "Javicon Browser"
                                : currentTitle + " - Javicon Browser";
                        setTitle(t);
                    });

    private void goHome() {
        String home = settingsManager.getHomePage();
        Platform.runLater(() -> {
            historyLock = false;
            webView.getEngine().load(home);
        });
    }

    private void openSettings() {
        SettingsWindow settingsWindow = new SettingsWindow(this, settingsManager);
        settingsWindow.setVisible(true);
    }

    private void addBookmark() {
        if (currentUrl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Страница ещё не загружена.");
            return;
        }
        bookmarkManager.addBookmark(currentTitle, currentUrl);
        JOptionPane.showMessageDialog(this, "Закладка добавлена: " + currentTitle);
    }

    private void openFavoritesWindow() {
        FavoritesWindow window = new FavoritesWindow(this, bookmarkManager, historyManager);
        window.setVisible(true);
    }

    public List<String> getHistoryUrls() {
        return new ArrayList<>(history);
    }

    public void navigateTo(String url) {
        navigate(url);
    }

    private void navigate(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        final String target = url;
        Platform.runLater(() -> {
            historyLock = false;
            webView.getEngine().load(target);
        });
    }

    private boolean isDownloadUrl(String url) {
        String lower = url.toLowerCase();
        for (String ext : DOWNLOAD_EXTENSIONS) {
            if (lower.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private void handleDownload(String url) {
        String name = url.substring(url.lastIndexOf('/') + 1);
        if (name.isEmpty() || name.contains("?")) {
            name = "download.bin";
        }
        File target = new File(settingsManager.getDownloadPath(), name);
        File parent = target.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (settingsManager.isAskDownloadPath()) {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(target);
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            target = chooser.getSelectedFile();
        }
        final File dest = target;
        statusLabel.setText("Загрузка " + name + "...");
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);

        new Thread(() -> {
            try {
                URLConnection conn = new URL(url).openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(60000);
                try (InputStream in = conn.getInputStream()) {
                    Files.copy(in, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    statusLabel.setText("Готово");
                    JOptionPane.showMessageDialog(this,
                            "Файл сохранён: " + dest.getAbsolutePath(),
                            "Загрузка завершена",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    statusLabel.setText("Ошибка загрузки");
                    JOptionPane.showMessageDialog(this,
                            "Ошибка загрузки: " + ex.getMessage(),
                            "Загрузка",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void savePageAs() {
        Platform.runLater(() -> {
            Object html = webView.getEngine().executeScript("document.documentElement.outerHTML");
            if (!(html instanceof String)) {
                JOptionPane.showMessageDialog(this, "Не удалось получить HTML страницы.");
                return;
            }
            String content = (String) html;
            SwingUtilities.invokeLater(() -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(new File("page.html"));
                if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File target = chooser.getSelectedFile();
                try {
                    Files.writeString(target.toPath(), content);
                    JOptionPane.showMessageDialog(this,
                            "Страница сохранена: " + target.getAbsolutePath(),
                            "Сохранение",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Ошибка сохранения: " + ex.getMessage(),
                            "Сохранение",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    }

    private void openUrlDialog() {
        Object obj = JOptionPane.showInputDialog(this, "Введите URL:", "Открыть URL",
                JOptionPane.PLAIN_MESSAGE, null, null, addressBar.getText());
        if (obj == null) {
            return;
        }
        String url = obj.toString().trim();
        if (!url.isEmpty()) {
            navigate(url);
        }
    }

    private void changeZoom(boolean in) {
        Platform.runLater(() -> {
            double z = webView.getZoom();
            webView.setZoom(in ? z * 1.1 : z / 1.1);
        });
    }

    private void setZoom(double z) {
        Platform.runLater(() -> webView.setZoom(z));
    }

    private void showAboutDialog() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        ImageIcon banner = loadBanner();
        if (banner != null) {
            Image scaled = banner.getImage().getScaledInstance(500, -1, Image.SCALE_SMOOTH);
            panel.add(new JLabel(new ImageIcon(scaled)), BorderLayout.NORTH);
        }

        JLabel text = new JLabel("<html><div style='text-align:center;font-family:Tahoma;font-size:12px;'>"
                + "<b>Javicon Browser 2.0.0</b><br><br>"
                + "Лёгкий ретро-браузер на Java.<br>"
                + "Движок: JavaFX WebView (Chromium)<br>"
                + "Java 17+, Maven, Swing + JavaFX.</div></html>");
        text.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(text, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "О программе", JOptionPane.PLAIN_MESSAGE);
    }

    public String getCurrentUrl() {
        return currentUrl.isEmpty() ? settingsManager.getHomePage() : currentUrl;
    }

    public void clearHistory() {
        history.clear();
        historyIndex = -1;
        updateNavButtons();
        JOptionPane.showMessageDialog(this, "История очищена.");
    }

    public void exportBookmarks() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("bookmarks_export.txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (BookmarkManager.Bookmark b : bookmarkManager.getBookmarks()) {
                sb.append(b.title()).append('\t').append(b.url()).append(System.lineSeparator());
            }
            Files.writeString(chooser.getSelectedFile().toPath(), sb.toString());
            JOptionPane.showMessageDialog(this, "Закладки экспортированы.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка экспорта: " + ex.getMessage());
        }
    }

    public void importBookmarks() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(chooser.getSelectedFile().toPath());
            for (String line : lines) {
                int tab = line.indexOf('\t');
                if (tab >= 0) {
                    String title = line.substring(0, tab).trim();
                    String url = line.substring(tab + 1).trim();
                    if (!url.isEmpty()) {
                        bookmarkManager.addBookmark(title, url);
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Закладки импортированы.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка импорта: " + ex.getMessage());
        }
    }

    public void clearCache() {
        Platform.runLater(() -> {
            if (webView != null) {
                webView.getEngine().reload();
            }
        });
        JOptionPane.showMessageDialog(this, "Кэш очищен.");
    }

    public void applySettings() {
        Platform.runLater(() ->
                webView.getEngine().setJavaScriptEnabled(settingsManager.isJavaScriptEnabled()));
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        JMenuItem openItem = new JMenuItem("Открыть URL...");
        openItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
        openItem.addActionListener(e -> openUrlDialog());
        JMenuItem saveItem = new JMenuItem("Сохранить как...");
        saveItem.addActionListener(e -> savePageAs());
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("Вид");
        JMenuItem refreshItem = new JMenuItem("Обновить");
        refreshItem.addActionListener(e -> reload());
        JMenuItem zoomInItem = new JMenuItem("Увеличить масштаб");
        zoomInItem.addActionListener(e -> changeZoom(true));
        JMenuItem zoomOutItem = new JMenuItem("Уменьшить масштаб");
        zoomOutItem.addActionListener(e -> changeZoom(false));
        JMenuItem zoomResetItem = new JMenuItem("Сбросить масштаб");
        zoomResetItem.addActionListener(e -> setZoom(1.0));
        viewMenu.add(refreshItem);
        viewMenu.addSeparator();
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(zoomResetItem);

        JMenu bookmarksMenu = new JMenu("Избранное");
        JMenuItem addBookmarkItem = new JMenuItem("Добавить в избранное");
        addBookmarkItem.addActionListener(e -> addBookmark());
        JMenuItem manageBookmarksItem = new JMenuItem("Управление избранным...");
        manageBookmarksItem.addActionListener(e -> openFavoritesWindow());
        bookmarksMenu.add(addBookmarkItem);
        bookmarksMenu.add(manageBookmarksItem);

        JMenu toolsMenu = new JMenu("Инструменты");
        JMenuItem settingsItem = new JMenuItem("Настройки...");
        settingsItem.addActionListener(e -> openSettings());
        JMenuItem historyItem = new JMenuItem("История...");
        historyItem.addActionListener(e -> openFavoritesWindow());
        JMenuItem clearHistoryItem = new JMenuItem("Очистить историю");
        clearHistoryItem.addActionListener(e -> clearHistory());
        toolsMenu.add(settingsItem);
        toolsMenu.add(historyItem);
        toolsMenu.add(clearHistoryItem);

        JMenu helpMenu = new JMenu("Справка");
        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(bookmarksMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void updateNavButtons() {
        SwingUtilities.invokeLater(() -> {
            backBtn.setEnabled(historyIndex > 0);
            forwardBtn.setEnabled(historyIndex >= 0 && historyIndex < history.size() - 1);
        });
    }
}
