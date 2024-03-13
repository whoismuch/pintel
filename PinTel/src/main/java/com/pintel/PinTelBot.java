package com.pintel;

import com.pintel.handler.CallbackQueryHandler;
import com.pintel.handler.MessageHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PinTelBot extends SpringWebhookBot {
    Logger logger = LoggerFactory.getLogger(PinTelBot.class);

    String botPath;
    String botUsername;
    String botToken;

    String initFilepath;

    MessageHandler messageHandler;
    CallbackQueryHandler callbackQueryHandler;

    public PinTelBot(SetWebhook setWebhook, MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler) {
        super(setWebhook);
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
    }


    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            logger.info("Webhook update was received");
            return handleUpdate(update);
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
            return new SendMessage(update.getMessage().getChatId().toString(), "Illegal message");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new SendMessage(update.getMessage().getChatId().toString(), e.getMessage());
        }
    }

    private BotApiMethod<?> handleUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        } else {
            Message message = update.getMessage();
            logger.info(update.getMessage().toString());
            if (message != null) {
                return messageHandler.answerMessage(update.getMessage());
            }
        }
        return null;
    }

}

