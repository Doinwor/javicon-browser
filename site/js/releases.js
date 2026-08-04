/*
 * Javicon Browser - автоматический список релизов.
 * Тянет все релизы с GitHub API и строит таблицу с кнопками загрузки.
 * Работает в публичном браузере без токенов (GitHub API включает CORS).
 */
document.addEventListener("DOMContentLoaded", function () {
  var notice = document.getElementById("releases-status");

  fetch("https://api.github.com/repos/Doinwor/javicon-browser/releases", {
    headers: { "Accept": "application/vnd.github+json" }
  })
    .then(function (res) {
      if (!res.ok) throw new Error("HTTP " + res.status);
      return res.json();
    })
    .then(function (releases) {
      if (!releases || !releases.length) {
        if (notice) notice.textContent = "Релизов пока нет.";
        return;
      }
      var latest = releases[0]; // GitHub отдаёт последний первым
      renderHero(latest);
      renderMain(releases);
      renderIndex(latest);
    })
    .catch(function (err) {
      if (notice) notice.textContent = "Не удалось получить список релизов: " + err.message;
    });
});

/* Кнопка-хедлайнер под баннером: последний релиз + его jar. */
function renderHero(rel) {
  var hero = document.getElementById("latest-release");
  if (!hero) return;
  var name = rel.name || rel.tag_name;
  var jar = null;
  var zip = null;
  (rel.assets || []).forEach(function (a) {
    if (/\.jar$/i.test(a.name)) jar = a;
    if (/\.zip$/i.test(a.name) && /windows/i.test(a.name)) zip = a;
  });
  var text = name.replace(/^Javicon\s*Browser\s*/i, "").trim();
  var h = "<span class='version-badge'>&#9733; ПОСЛЕДНЯЯ ВЕРСИЯ</span> "
    + "<h2 style='margin:6px 0'>" + esc(text || name) + "</h2><br>";
  if (jar) h += "<a class=\"button green\" href=\"" + jar.browser_download_url + "\" target=\"_blank\">&#8681; СКАЧАТЬ JAR</a> ";
  if (zip) h += "<a class=\"button\" href=\"" + zip.browser_download_url + "\" target=\"_blank\">&#8681; СКАЧАТЬ EXE (Windows)</a> ";
  hero.innerHTML = h;
}

/* Полная таблица всех релизов и их ассетов. */
function renderMain(releases) {
  var tbody = document.querySelector("#releases-table tbody");
  if (!tbody) return;
  var rows = "";
  releases.forEach(function (rel, idx) {
    var name = rel.name || rel.tag_name;
    var tag = rel.tag_name;
    var date = fmtDate(rel.published_at);
    var badge = idx === 0 ? "<span class=\"version-badge\">&#9733;</span> " : "";
    var assets = rel.assets || [];
    if (!assets.length) {
      rows += "<tr><td>" + badge + esc(name) + "</td>"
        + "<td>" + date + "</td>"
        + "<td colspan=\"3\">Нет файлов для загрузки</td></tr>";
      return;
    }
    (assets || []).forEach(function (asset) {
      var platform = "Все ОС";
      if (/windows/i.test(asset.name)) platform = "Windows";
      else if (/linux/i.test(asset.name)) platform = "Linux";
      else if (/mac/i.test(asset.name)) platform = "macOS";
      var size = readable(asset.size);
      rows += "<tr>"
        + "<td>" + badge + "<b>" + esc(tag) + "</b></td>"
        + "<td>" + date + "</td>"
        + "<td>" + esc(asset.name) + "</td>"
        + "<td>" + size + "</td>"
        + "<td>" + platform + "</td>"
        + "<td><a class=\"button\" href=\"" + asset.browser_download_url + "\" target=\"_blank\">Скачать</a></td>"
        + "</tr>";
    });
  });
  tbody.innerHTML = rows;
}

/* Дата релиза в формате ДД.ММ.ГГГГ */
function fmtDate(iso) {
  if (!iso) return "";
  var d = new Date(iso);
  if (isNaN(d.getTime())) return "";
  var dd = ("0" + d.getDate()).slice(-2);
  var mm = ("0" + (d.getMonth() + 1)).slice(-2);
  return dd + "." + mm + "." + d.getFullYear();
}

function readable(n) {
  if (!n) return "";
  var mb = n / (1024 * 1024);
  return (mb >= 1 ? mb.toFixed(1) : (n / 1024).toFixed(0)) + (mb >= 1 ? " МБ" : " КБ");
}

function esc(s) {
  return String(s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

/* Актуальная версия на главной странице. */
function renderIndex(rel) {
  var name = document.getElementById("latest-version-name");
  var platform = document.getElementById("latest-version-platform");
  var req = document.getElementById("latest-version-req");
  if (!name) return;
  var text = rel.name || rel.tag_name;
  if (name) name.textContent = text;
  if (platform) platform.textContent = "Windows / Linux / macOS";
  if (req) req.textContent = "Java JDK 17+";
}