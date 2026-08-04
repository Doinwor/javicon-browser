/*
 * Javicon Browser - реальный счётчик посетителей.
 * Использует бесплатный CounterAPI (counterapi.dev, без регистрации).
 * Каждый заход инкрементирует счётчик и показывает его с ведущими нулями.
 */
document.addEventListener("DOMContentLoaded", function () {
  var el = document.getElementById("visitor-counter");
  if (!el) return;

  startDots(el);

  var attempts = 0;
  function load() {
    var controller = new AbortController();
    var timeout = setTimeout(function () { controller.abort(); }, 8000);
    fetch("https://api.counterapi.dev/v1/javicon/javicon-browser-visits/up", {
      method: "GET",
      signal: controller.signal
    })
      .then(function (res) { return res.ok ? res.json() : null; })
      .then(function (data) {
        clearTimeout(timeout);
        if (data && typeof data.count !== "undefined") {
          el.textContent = pad(data.count, 6);
        } else if (attempts < 2) {
          attempts++;
          setTimeout(load, 1500);
        } else {
          el.innerHTML = "<span class=\"dots\" id=\"counter-dots\">&middot;&middot;</span>";
        }
      })
      .catch(function () {
        clearTimeout(timeout);
        if (attempts < 2) {
          attempts++;
          setTimeout(load, 1500);
        } else {
          el.innerHTML = "<span class=\"dots\" id=\"counter-dots\">&middot;&middot;</span>";
        }
      });
  }
  load();
});

/* Анимированные "загружающиеся" точечки-заполнитель. */
function startDots(el) {
  var frames = ["·", "··", "···", "····", "·····", "······"];
  var i = 0;
  var timer = setInterval(function () {
    if (!document.getElementById("counter-dots")) { clearInterval(timer); return; }
    el.innerHTML = "<span class=\"dots\" id=\"counter-dots\">" + frames[i] + "</span>";
    i = (i + 1) % frames.length;
  }, 350);
}

function pad(n, len) {
  var s = String(n);
  while (s.length < len) s = "0" + s;
  return s;
}