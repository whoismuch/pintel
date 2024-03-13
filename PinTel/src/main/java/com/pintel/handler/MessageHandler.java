package com.pintel.handler;

import com.pintel.exception.CommandNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {
    final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString();
        SendMessage answerMessage = null;
        try {
            if (message.getText() == null) {
                throw new IllegalArgumentException();
            }
            String inputText = message.getText().trim();
            answerMessage = new SendMessage(chatId, inputText);
        } catch (CommandNotFoundException e) {
            answerMessage = new SendMessage(chatId, e.getMessage());
        }
        return answerMessage;
    }
}
