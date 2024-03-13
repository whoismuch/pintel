package com.pintel.handler;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallbackQueryHandler {

    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {
        final String chartId = callbackQuery.getMessage().getChatId().toString();
        return new SendMessage(chartId, "Callback query proceeded");
    }
}
