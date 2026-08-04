/*
 * Javicon Browser - реальный счётчик посетителей.
 * Использует бесплатный CounterAPI (counterapi.dev, без регистрации).
 * Каждый заход инкрементирует счётчик и показывает его с ведущими нулями.
 */
document.addEventListener("DOMContentLoaded", function () {
  var el = document.getElementById("visitor-counter");
  if (!el) return;

  startDots(el);

  fetch("https://api.counterapi.dev/v1/javicon/javicon-browser-visits/up", {
    method: "GET"
  })
    .then(function (res) { return res.ok ? res.json() : null; })
    .then(function (data) {
      if (data && typeof data.count !== "undefined") {
        el.textContent = pad(data.count, 6);
      }
    })
    .catch(function () {
      /* Оставляем точечки, если счётчик недоступен */
    });
});

/* Анимированные "загружающиеся" точечки-заполнитель. */
function startDots(el) {
  var frames = ["·", "··", "···", "····", "·····", "······"];
  var i = 0;
  var timer = setInterval(function () {
    if (!document.getElementById("counter-dots")) { clearInterval(timer); return; }
    el.innerHTML = "<span class=\"dots\" id=\"counter-dots\">" + frames[i] + "</span>";
    i = (i + 1) % frames.length;
  }, 400);
}

function pad(n, len) {
  var s = String(n);
  while (s.length < len) s = "0" + s;
  return s;
}