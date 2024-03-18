Чтобы проверить работоспособность телеграм бота можно использовать ngrok в качестве reversed proxy
### Настройка ngrok
1. Скачать client для ngrok и создать учетную запись на сайте https://ngrok.com/
2. Получить там auto-token
3. При первом запуске `ngrok config add-authtoken <autotoken>`

### Запуск телеграм бота
1. Запустить ngrok `ngrok http http://localhost:8083 --host-header=localhost`
2. После запуска вы получите адрес, с которого все запросы будут форвардиться на ваш localhost. Например, вот такого вида `https://8d0e-31-134-188-180.ngrok-free.app`
3. Нужно прописать этот адрес в файле **application.properties** для `telegram.webhook-path`
4. Открываем Postman и для того, чтобы установить webhook для телеграм бота, отправляем POST запрос `https://api.telegram.org/bot6940539957:AAFTK2PcKutWgb5QCD7jzXlnCmfRk4rO14Y/setWebhook?url=<url>/telegram/api`, где вместо `<url>` прописываем полученный с ngrok url, то есть все тот же `https://8d0e-31-134-188-180.ngrok-free.app`. Здесь `6940539957:AAFTK2PcKutWgb5QCD7jzXlnCmfRk4rO14Y` -- это токен телеграм бота. Если все правильно, то должыен прийти ответ со статусом 200 и сообщением `Webhook was set`
5. Запускаем spring boot приложение
6. Переходим в телеграм, открываем чат с ботом https://t.me/PinTel4268gyt87gBot и пишем /start
