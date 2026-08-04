/*
 * Javicon Browser - реальный счётчик посетителей.
 * Использует бесплатный CounterAPI (counterapi.dev, без регистрации).
 * Каждый заход инкрементирует счётчик и показывает его с ведущими нулями.
 */
document.addEventListener("DOMContentLoaded", function () {
  var el = document.getElementById("visitor-counter");
  if (!el) return;

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
      /* Оставляем статичное число, если счётчик недоступен */
    });
});

function pad(n, len) {
  var s = String(n);
  while (s.length < len) s = "0" + s;
  return s;
}