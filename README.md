# 🌐 Javicon Browser

> Лёгкий ретро-браузер на Java. Выглядит как Internet Explorer 5/6, работает на современном движке **JavaFX WebView (Chromium)**.

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/technologies/downloads/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0.0-brightgreen.svg)](https://github.com/Doinwor/javicon-browser/releases)

---

## ✨ Возможности

### Олдскульный интерфейс
- Классический вид Internet Explorer 5/6: серая панель инструментов, рельефные кнопки, статус-бар «Готово».
- Системный Look-and-Feel Windows, шрифт Tahoma 11.
- Полноценное окно настроек с вкладками (как в старых браузерах).

### Современный движок
- Полная поддержка HTML5, CSS3, ES6 благодаря встроенному Chromium.
- Масштабирование страницы (Вид → Увеличить/Уменьшить масштаб).

### Навигация и история
- Кнопки «Назад / Вперёд / Обновить / Домой».
- История посещений, сохраняемая между сессиями.
- Восстановление последней сессии при запуске.

### Закладки
- Добавление текущей страницы в избранное.
- Диалог управления закладками (открыть / удалить).
- Импорт и экспорт закладок.

### Загрузки и сохранение
- Загрузка файлов с выбором папки.
- «Сохранить как...» — сохранение страницы в HTML.
- Путь для загрузок настраивается.

### Настройки
- Домашняя страница, поисковая система, поведение при запуске.
- Тема, шрифт, размер панели инструментов.
- История, закладки, папка загрузок.
- Прокси, размер кэша, JavaScript, безопасность.

---

## 📥 Установка

### Вариант 1 — Готовый JAR
1. Установите [JDK 17+](https://adoptium.net/).
2. Скачайте `javicon-browser.jar` из [Releases](https://github.com/Doinwor/javicon-browser/releases).
3. Запустите:
   ```bash
   java -jar javicon-browser.jar
   ```

### Вариант 2 — Windows EXE (portable)
1. Скачайте `JaviconBrowser-windows.zip` из [Releases](https://github.com/Doinwor/javicon-browser/releases).
2. Распакуйте и запустите `JaviconBrowser\JaviconBrowser.exe`.
3. Java уже включена в пакет — устанавливать ничего не нужно.

### Вариант 3 — Из исходников
```bash
git clone https://github.com/Doinwor/javicon-browser.git
cd javicon-browser
mvn clean package
java -jar target/javicon-browser.jar
```

---

## 🛠 Сборка

Требования: **JDK 17+**, **Maven 3.9+**.

```bash
mvn clean package        # собрать fat JAR в target/
```

Сборка переносимого EXE для Windows:

```bash
jpackage --type app-image --name "JaviconBrowser" --app-version "1.0.0" \
  --vendor "Javicon Project" --icon app.ico \
  --input target --main-jar javicon-browser.jar \
  --main-class com.javicon.browser.Main --dest dist
```

---

## 📁 Структура проекта

```
javicon-browser
├── pom.xml                      # Maven + JavaFX + Shade
├── run.bat / run.sh             # скрипты запуска
├── README.md
└── src/main/java/com/javicon/browser/
    ├── Main.java                # точка входа
    ├── BrowserWindow.java       # главное окно
    ├── SettingsWindow.java      # окно настроек
    ├── SettingsManager.java     # настройки (.javicon/settings.properties)
    ├── HistoryManager.java      # история (history.txt)
    └── BookmarkManager.java     # закладки (bookmarks.txt)
```

---

## ⚙️ Настройки и данные

Все данные хранятся в папке пользователя:

| Файл | Назначение |
|------|-----------|
| `~/.javicon/settings.properties` | Настройки браузера |
| `~/.javicon/history.txt` | История посещений |
| `~/.javicon/bookmarks.txt` | Закладки |

---

## 🗺 Дорожная карта

- [x] Базовый браузер (WebView, адресная строка)
- [x] Навигация и история
- [x] Домашняя страница и настройки
- [x] Закладки
- [x] Меню и диалоги
- [x] Загрузки и статус-бар
- [x] История между сессиями
- [x] Олдскульный стиль
- [x] Полноценное окно настроек
- [ ] Система плагинов
- [ ] Установщик (jpackage MSI/EXE)

---

## 📄 Лицензия

Проект распространяется под лицензией **MIT**. Подробности в файле [LICENSE](LICENSE).

---

## 🌐 Сайт проекта

Официальный сайт: [github.com/Doinwor/javicon-browser](https://github.com/Doinwor/javicon-browser) — исходный код, релизы и документация.

Made with ❤️ и ретро-ностальгией. 1998-2026.