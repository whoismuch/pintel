package com.pintel.handler;

import com.pintel.PinTelBot;
import com.pintel.constants.BotCommandEnum;
import com.pintel.constants.BotMessageEnum;
import com.pintel.exception.CommandNotFoundException;
import com.pintel.keyboards.InlineKeyboardMaker;
import com.pintel.service.TgUserService;
import com.pintel.util.MessageUtils;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {
    final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    final TgUserService userService;
    final MessageUtils messageUtils;

    public BotApiMethod<?> answerMessage(PinTelBot bot, Message message) {
        String chatId = message.getChatId().toString();
        Long userId = message.getFrom().getId();
        try {
            String inputText = message.getText() != null ? message.getText().toLowerCase().trim() : null;
            BotCommandEnum command = BotCommandEnum.getCommand(inputText);
            if (command != null) {
                return processCommand(command, userId, chatId);
            } else if (userService.getLastCommand(userId).equals(BotCommandEnum.MAKE_SELECTION.getCommandName())) {
                return processMakeSelection(bot, message, inputText, userId, chatId);
            } else {
                return messageUtils.getSendMessage(chatId, BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE);
            }
        } catch (CommandNotFoundException | TelegramApiException e) {
            logger.warn("Illegal message: " + e.getMessage());
            return messageUtils.getSendMessage(chatId, BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE);
        }
    }

    private SendMessage processCommand(BotCommandEnum commandEnum, Long userId, String chatId) {
        SendMessage message = switch(commandEnum) {
            case START -> {
                userService.addUser(userId, null, chatId, BotCommandEnum.START.getCommandName());
                yield messageUtils.getSendMessage(chatId, BotMessageEnum.HELP_MESSAGE);
            }
            case HELP -> new SendMessage(chatId, BotMessageEnum.HELP_MESSAGE.getText());
            case MAKE_SELECTION -> messageUtils.chooseSelectionType(chatId);
        };
        saveLastCommand(commandEnum, userId);
        return message;
    }

    private SendMessage processMakeSelection(PinTelBot bot, Message message, String inputText, Long userId, String chatId) throws TelegramApiException {
         if (inputText != null && userService.getSelectionType(userId) == null) {
            return messageUtils.chooseSelectionType(chatId);
        } else if (message.hasPhoto()) {
            List<PhotoSize> photo = message.getPhoto();
            // todo: get selection of images from service
            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(chatId)
                    .photo(new InputFile(new File("src/main/resources/img.png")))
                    .caption(BotMessageEnum.RESULT_SELECTION.getText())
                    .build();
            bot.execute(sendPhoto);
            userService.saveSelectionType(userId, null);
            return new SendMessage();
        }
        return messageUtils.getLoadImageMessage(chatId, userService.getSelectionType(userId));
    }

    private void saveLastCommand(@Nullable BotCommandEnum commandEnum, Long userId) {
        if (commandEnum != null && commandEnum != BotCommandEnum.HELP) {
            userService.saveLastCommand(userId, commandEnum.getCommandName());
        }
    }

}
