package com.pintel;

import com.pintel.constants.BotMessageEnum;
import com.pintel.handler.CallbackQueryHandler;
import com.pintel.handler.MessageHandler;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.*;
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
            logger.warn("Illegal message received: " + e.getMessage());
            return new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE.getText());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new SendMessage(update.getMessage().getChatId().toString(),
                    BotMessageEnum.EXCEPTION_STH_GOES_WRONG_MESSAGE.getText() + ": " + e.getMessage());
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
                return messageHandler.answerMessage(this, update.getMessage());
            }
        }
        return null;
    }
}

