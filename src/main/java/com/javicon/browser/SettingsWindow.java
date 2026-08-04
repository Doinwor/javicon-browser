package com.javicon.browser;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Image;

public class SettingsWindow extends JDialog {

    private final SettingsManager settings;
    private final BrowserWindow parent;

    private JTextField homeField;
    private JComboBox<String> searchEngineCombo;
    private JCheckBox openHomeOnStart;
    private JCheckBox restoreSession;

    private JRadioButton classicTheme;
    private JRadioButton darkTheme;
    private JRadioButton lightTheme;
    private JComboBox<String> fontCombo;
    private JComboBox<String> toolbarSizeCombo;

    private JCheckBox saveHistory;
    private JCheckBox saveHistoryBetweenSessions;
    private JCheckBox showBookmarksBar;
    private JCheckBox askDownloadPath;
    private JTextField downloadPathField;
    private JButton browseDownloadPathBtn;

    private JComboBox<String> proxyCombo;
    private JTextField proxyHostField;
    private JTextField proxyPortField;
    private JTextField cacheField;
    private JCheckBox javaScriptEnabled;
    private JCheckBox blockDangerousSites;

    public SettingsWindow(BrowserWindow parent, SettingsManager settings) {
        super(parent, "Настройки Javicon Browser", true);
        this.parent = parent;
        this.settings = settings;
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        initUI();
        loadSettings();
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
        tabs.addTab("Общие", createGeneralPanel());
        tabs.addTab("Внешний вид", createAppearancePanel());
        tabs.addTab("Поведение", createBehaviorPanel());
        tabs.addTab("Расширенные", createAdvancedPanel());

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
        JButton okBtn = new JButton("OK");
        JButton applyBtn = new JButton("Применить");
        JButton cancelBtn = new JButton("Отмена");
        JButton resetBtn = new JButton("Сбросить настройки");

        okBtn.addActionListener(e -> {
            saveSettings();
            dispose();
        });
        applyBtn.addActionListener(e -> saveSettings());
        cancelBtn.addActionListener(e -> dispose());
        resetBtn.addActionListener(e -> {
            settings.resetToDefaults();
            loadSettings();
        });

        buttonPanel.add(resetBtn);
        buttonPanel.add(okBtn);
        buttonPanel.add(applyBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Домашняя страница:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        homeField = new JTextField(30);
        panel.add(homeField, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 1;
        JButton useCurrentBtn = new JButton("Использовать текущую");
        useCurrentBtn.addActionListener(e -> homeField.setText(parent.getCurrentUrl()));
        panel.add(useCurrentBtn, gbc);
        gbc.gridx = 2;
        JButton defaultBtn = new JButton("По умолчанию");
        defaultBtn.addActionListener(e -> homeField.setText("https://www.google.com"));
        panel.add(defaultBtn, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Поисковая система:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        searchEngineCombo = new JComboBox<>(new String[]{"Google", "Яндекс", "Bing", "DuckDuckGo"});
        searchEngineCombo.setEditable(true);
        panel.add(searchEngineCombo, gbc);

        gbc.gridwidth = 2;
        gbc.gridy = 3;
        gbc.gridx = 0;
        openHomeOnStart = new JCheckBox("Открывать домашнюю страницу при запуске");
        panel.add(openHomeOnStart, gbc);

        gbc.gridy = 4;
        restoreSession = new JCheckBox("Восстанавливать предыдущую сессию");
        panel.add(restoreSession, gbc);

        return panel;
    }

    private JPanel createAppearancePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Тема оформления:"), gbc);
        gbc.gridx = 1;
        ButtonGroup themeGroup = new ButtonGroup();
        classicTheme = new JRadioButton("Классическая (Windows)");
        darkTheme = new JRadioButton("Тёмная");
        lightTheme = new JRadioButton("Светлая");
        themeGroup.add(classicTheme);
        themeGroup.add(darkTheme);
        themeGroup.add(lightTheme);
        JPanel themePanel = new JPanel(new GridLayout(1, 3));
        themePanel.add(classicTheme);
        themePanel.add(darkTheme);
        themePanel.add(lightTheme);
        panel.add(themePanel, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Шрифт:"), gbc);
        gbc.gridx = 1;
        fontCombo = new JComboBox<>(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        panel.add(fontCombo, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Панель инструментов:"), gbc);
        gbc.gridx = 1;
        toolbarSizeCombo = new JComboBox<>(new String[]{"Маленькие", "Обычные", "Крупные"});
        panel.add(toolbarSizeCombo, gbc);

        return panel;
    }

    private JPanel createBehaviorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        saveHistory = new JCheckBox("Сохранять историю посещений");
        panel.add(saveHistory, gbc);

        gbc.gridy = 1;
        saveHistoryBetweenSessions = new JCheckBox("Сохранять историю между сессиями");
        panel.add(saveHistoryBetweenSessions, gbc);

        gbc.gridy = 2;
        JButton clearHistoryBtn = new JButton("Очистить историю");
        clearHistoryBtn.addActionListener(e -> parent.clearHistory());
        panel.add(clearHistoryBtn, gbc);

        gbc.gridy = 3;
        showBookmarksBar = new JCheckBox("Показывать панель закладок");
        panel.add(showBookmarksBar, gbc);

        gbc.gridy = 4;
        JButton exportBookmarksBtn = new JButton("Экспортировать закладки");
        exportBookmarksBtn.addActionListener(e -> parent.exportBookmarks());
        panel.add(exportBookmarksBtn, gbc);
        gbc.gridy = 5;
        JButton importBookmarksBtn = new JButton("Импортировать закладки");
        importBookmarksBtn.addActionListener(e -> parent.importBookmarks());
        panel.add(importBookmarksBtn, gbc);

        gbc.gridy = 6;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Папка для загрузок:"), gbc);
        gbc.gridx = 1;
        JPanel downloadPanel = new JPanel(new BorderLayout(5, 0));
        downloadPathField = new JTextField();
        browseDownloadPathBtn = new JButton("Обзор...");
        browseDownloadPathBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                downloadPathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        downloadPanel.add(downloadPathField, BorderLayout.CENTER);
        downloadPanel.add(browseDownloadPathBtn, BorderLayout.EAST);
        panel.add(downloadPanel, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        askDownloadPath = new JCheckBox("Спрашивать куда сохранять каждый файл");
        panel.add(askDownloadPath, gbc);

        return panel;
    }

    private JPanel createAdvancedPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Прокси:"), gbc);
        gbc.gridx = 1;
        proxyCombo = new JComboBox<>(new String[]{"Без прокси", "Автоматически", "Ручная настройка"});
        panel.add(proxyCombo, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Адрес прокси:"), gbc);
        gbc.gridx = 1;
        proxyHostField = new JTextField(15);
        panel.add(proxyHostField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Порт:"), gbc);
        gbc.gridx = 1;
        proxyPortField = new JTextField(8);
        panel.add(proxyPortField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Размер кэша (МБ):"), gbc);
        gbc.gridx = 1;
        cacheField = new JTextField(8);
        panel.add(cacheField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        JButton clearCacheBtn = new JButton("Очистить кэш");
        clearCacheBtn.addActionListener(e -> parent.clearCache());
        panel.add(clearCacheBtn, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        javaScriptEnabled = new JCheckBox("Включить JavaScript");
        panel.add(javaScriptEnabled, gbc);

        gbc.gridy = 6;
        blockDangerousSites = new JCheckBox("Блокировать опасные сайты");
        panel.add(blockDangerousSites, gbc);

        return panel;
    }

    private void loadSettings() {
        homeField.setText(settings.getHomePage());
        searchEngineCombo.setSelectedItem(settings.getSearchEngine());
        openHomeOnStart.setSelected(settings.isOpenHomeOnStart());
        restoreSession.setSelected(settings.isRestoreSession());

        String theme = settings.getTheme();
        if (theme.equals("Тёмная")) {
            darkTheme.setSelected(true);
        } else if (theme.equals("Светлая")) {
            lightTheme.setSelected(true);
        } else {
            classicTheme.setSelected(true);
        }
        fontCombo.setSelectedItem(settings.getFontName());
        toolbarSizeCombo.setSelectedItem(settings.getToolbarSize());

        saveHistory.setSelected(settings.isSaveHistory());
        saveHistoryBetweenSessions.setSelected(settings.isSaveHistoryBetweenSessions());
        showBookmarksBar.setSelected(settings.isShowBookmarksBar());
        askDownloadPath.setSelected(settings.isAskDownloadPath());
        downloadPathField.setText(settings.getDownloadPath());

        proxyCombo.setSelectedItem(settings.getProxy());
        proxyHostField.setText(settings.getProxyHost());
        proxyPortField.setText(settings.getProxyPort());
        cacheField.setText(settings.getCacheSize());
        javaScriptEnabled.setSelected(settings.isJavaScriptEnabled());
        blockDangerousSites.setSelected(settings.isBlockDangerousSites());
    }

    private void saveSettings() {
        settings.setHomePage(homeField.getText().trim());
        settings.setSearchEngine((String) searchEngineCombo.getSelectedItem());
        settings.setOpenHomeOnStart(openHomeOnStart.isSelected());
        settings.setRestoreSession(restoreSession.isSelected());

        settings.setTheme(classicTheme.isSelected() ? "Классическая (Windows)"
                : darkTheme.isSelected() ? "Тёмная" : "Светлая");
        settings.setFontName((String) fontCombo.getSelectedItem());
        settings.setToolbarSize((String) toolbarSizeCombo.getSelectedItem());

        settings.setSaveHistory(saveHistory.isSelected());
        settings.setSaveHistoryBetweenSessions(saveHistoryBetweenSessions.isSelected());
        settings.setShowBookmarksBar(showBookmarksBar.isSelected());
        settings.setAskDownloadPath(askDownloadPath.isSelected());
        settings.setDownloadPath(downloadPathField.getText().trim());

        settings.setProxy((String) proxyCombo.getSelectedItem());
        settings.setProxyHost(proxyHostField.getText().trim());
        settings.setProxyPort(proxyPortField.getText().trim());
        settings.setCacheSize(cacheField.getText().trim());
        settings.setJavaScriptEnabled(javaScriptEnabled.isSelected());
        settings.setBlockDangerousSites(blockDangerousSites.isSelected());

        settings.save();
        parent.applySettings();
    }
}
