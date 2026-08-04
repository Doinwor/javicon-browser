package com.javicon.browser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsManager {

    private static final File SETTINGS_FILE =
            new File(System.getProperty("user.home") + "/.javicon/settings.properties");

    private static final String DEFAULT_HOME = "https://www.google.com";
    private static final String DEFAULT_DOWNLOAD_PATH =
            System.getProperty("user.home") + "/Downloads";
    private static final String DEFAULT_FONT = "Tahoma";

    private final Properties properties = new Properties();

    public SettingsManager() {
        load();
    }

    public void load() {
        if (!SETTINGS_FILE.exists()) {
            return;
        }
        try (FileInputStream in = new FileInputStream(SETTINGS_FILE)) {
            properties.load(in);
        } catch (IOException e) {
            System.err.println("Не удалось загрузить настройки: " + e.getMessage());
        }
    }

    public void save() {
        File dir = SETTINGS_FILE.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(out, "Javicon Browser settings");
        } catch (IOException e) {
            System.err.println("Не удалось сохранить настройки: " + e.getMessage());
        }
    }

    // ---------- Домашняя страница ----------

    public String getHomePage() {
        return properties.getProperty("homePage", DEFAULT_HOME);
    }

    public void setHomePage(String url) {
        properties.setProperty("homePage", url);
    }

    // ---------- Поисковая система ----------

    public String getSearchEngine() {
        return properties.getProperty("searchEngine", "Google");
    }

    public void setSearchEngine(String engine) {
        properties.setProperty("searchEngine", engine);
    }

    // ---------- Запуск ----------

    public boolean isOpenHomeOnStart() {
        return getBoolean("openHomeOnStart", true);
    }

    public void setOpenHomeOnStart(boolean value) {
        properties.setProperty("openHomeOnStart", Boolean.toString(value));
    }

    public boolean isRestoreSession() {
        return getBoolean("restoreSession", true);
    }

    public void setRestoreSession(boolean value) {
        properties.setProperty("restoreSession", Boolean.toString(value));
    }

    // ---------- Тема / внешний вид ----------

    public String getTheme() {
        return properties.getProperty("theme", "Классическая (Windows)");
    }

    public void setTheme(String theme) {
        properties.setProperty("theme", theme);
    }

    public String getFontName() {
        return properties.getProperty("fontName", DEFAULT_FONT);
    }

    public void setFontName(String fontName) {
        properties.setProperty("fontName", fontName);
    }

    public String getToolbarSize() {
        return properties.getProperty("toolbarSize", "Обычные");
    }

    public void setToolbarSize(String size) {
        properties.setProperty("toolbarSize", size);
    }

    // ---------- История ----------

    public boolean isSaveHistory() {
        return getBoolean("saveHistory", true);
    }

    public void setSaveHistory(boolean value) {
        properties.setProperty("saveHistory", Boolean.toString(value));
    }

    public boolean isSaveHistoryBetweenSessions() {
        return getBoolean("saveHistoryBetweenSessions", true);
    }

    public void setSaveHistoryBetweenSessions(boolean value) {
        properties.setProperty("saveHistoryBetweenSessions", Boolean.toString(value));
    }

    // ---------- Закладки ----------

    public boolean isShowBookmarksBar() {
        return getBoolean("showBookmarksBar", false);
    }

    public void setShowBookmarksBar(boolean value) {
        properties.setProperty("showBookmarksBar", Boolean.toString(value));
    }

    // ---------- Загрузки ----------

    public String getDownloadPath() {
        return properties.getProperty("downloadPath", DEFAULT_DOWNLOAD_PATH);
    }

    public void setDownloadPath(String path) {
        properties.setProperty("downloadPath", path);
    }

    public boolean isAskDownloadPath() {
        return getBoolean("askDownloadPath", true);
    }

    public void setAskDownloadPath(boolean value) {
        properties.setProperty("askDownloadPath", Boolean.toString(value));
    }

    // ---------- Прокси ----------

    public String getProxy() {
        return properties.getProperty("proxy", "Без прокси");
    }

    public void setProxy(String proxy) {
        properties.setProperty("proxy", proxy);
    }

    public String getProxyHost() {
        return properties.getProperty("proxyHost", "");
    }

    public void setProxyHost(String host) {
        properties.setProperty("proxyHost", host);
    }

    public String getProxyPort() {
        return properties.getProperty("proxyPort", "8080");
    }

    public void setProxyPort(String port) {
        properties.setProperty("proxyPort", port);
    }

    // ---------- Кэш ----------

    public String getCacheSize() {
        return properties.getProperty("cacheSize", "50");
    }

    public void setCacheSize(String size) {
        properties.setProperty("cacheSize", size);
    }

    // ---------- JavaScript / безопасность ----------

    public boolean isJavaScriptEnabled() {
        return getBoolean("javaScriptEnabled", true);
    }

    public void setJavaScriptEnabled(boolean value) {
        properties.setProperty("javaScriptEnabled", Boolean.toString(value));
    }

    public boolean isBlockDangerousSites() {
        return getBoolean("blockDangerousSites", false);
    }

    public void setBlockDangerousSites(boolean value) {
        properties.setProperty("blockDangerousSites", Boolean.toString(value));
    }

    // ---------- Утилиты ----------

    private boolean getBoolean(String key, boolean def) {
        String v = properties.getProperty(key);
        return (v == null) ? def : Boolean.parseBoolean(v);
    }

    public void resetToDefaults() {
        properties.clear();
    }
}
