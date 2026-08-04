# Как включить GitHub Wiki (сделать один раз)

Репозиторий wiki GitHub создаёт автоматически при первом открытии страницы
`https://github.com/Doinwor/javicon-browser/wiki` — **в браузере, будучи залогиненным**.

## Шаги

1. Откройте в браузере (войдите в аккаунт Doinwor):
   https://github.com/Doinwor/javicon-browser/wiki

2. GitHub покажет «Welcome to the wiki!» с предложением создать первую страницу.
   Нажмите **Create the first page** («Создать первую страницу»).

3. В редакторе ничего менять не надо — просто нажмите кнопку
   **Save Page** / **Сохранить страницу** внизу.

4. Готово. Это создаст репозиторий `Doinwor/javicon-browser.wiki`.

## После этого

Все готовые страницы wiki лежат в папке `D:\javicon\javicon-browser\.wiki\`
(это отдельный git-репозиторий, коммит уже сделан).

Осталось запушить их:

```powershell
cd D:\javicon\javicon-browser\.wiki
git remote add origin https://github.com/Doinwor/javicon-browser.wiki.git
git push -u origin master
```

Либо просто скажи мне («запушь вики») — я выполню команду за тебя,
как только репозиторий появится на GitHub.

## Мой счётчик

После активации вики я запушу эти страницы:
Home, О проекте, Возможности, Установка, Технические детали,
История версий, Ссылки + sidebar/footer.