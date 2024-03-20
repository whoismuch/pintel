package com.pintel.handler;

import com.pintel.PinTelBot;
import com.pintel.constants.BotCommandEnum;
import com.pintel.constants.BotMessageEnum;
import com.pintel.exception.CommandNotFoundException;
import com.pintel.keyboards.ReplyKeyboardMaker;
import com.pintel.service.TgUserService;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
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
import java.util.Map;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {
    final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    final TgUserService userService;

    @Autowired
    ReplyKeyboardMaker replyKeyboardMaker;

    final ApplicationContext context;
    final List<String> selectionTypes = List.of(BotMessageEnum.CHOOSE_TYPE_CONCEPT.getText().toLowerCase(), BotMessageEnum.CHOOSE_TYPE_COLOR.getText().toLowerCase());

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
                return getSendMessage(chatId, BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE);
            }
        } catch (CommandNotFoundException | TelegramApiException e) {
            logger.warn("Illegal message: " + e.getMessage());
            return getSendMessage(chatId, BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE);
        }
    }

    private SendMessage processCommand(BotCommandEnum commandEnum, Long userId, String chatId) {
        SendMessage message = switch(commandEnum) {
            case START -> {
                userService.addUser(userId, null, chatId, BotCommandEnum.START.getCommandName());
                yield getSendMessage(chatId, BotMessageEnum.HELP_MESSAGE);
            }
            case HELP -> new SendMessage(chatId, BotMessageEnum.HELP_MESSAGE.getText());
            case MAKE_SELECTION -> chooseSelectionType(chatId);
            default -> null;
        };
        saveLastCommand(commandEnum, userId);
        return message;
    }

    public void processCommand(BotCommandEnum commandEnum, Map<String, ?> chatMessageMap) {
       switch(commandEnum) {
            case SEND_NEWSLETTER -> sendToAllUsers(chatMessageMap);
       };
    }


    private SendMessage chooseSelectionType(String chatId) {
        List<String> types = List.of(BotMessageEnum.CHOOSE_TYPE_CONCEPT.getText(), BotMessageEnum.CHOOSE_TYPE_COLOR.getText());
        SendMessage sendMessage = getSendMessage(chatId, BotMessageEnum.CHOOSE_SELECTION_TYPE);
//        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getReplyMessageButtons(types));
        return sendMessage;
    }

    private SendMessage processMakeSelection(PinTelBot bot, Message message, String inputText, Long userId, String chatId) throws TelegramApiException {
         if (inputText != null && selectionTypes.contains(inputText)) {
            userService.saveSelectionType(userId, inputText);
            SendMessage answerMessage = getSendMessage(chatId, BotMessageEnum.LOAD_IMAGE);
            answerMessage.setReplyMarkup(null);
            return answerMessage;
        } else if (inputText != null && userService.getSelectionType(userId) == null) {
            return chooseSelectionType(chatId);
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
        return getSendMessage(chatId, BotMessageEnum.LOAD_IMAGE);
    }

    private SendMessage getSendMessage(String chatId, BotMessageEnum messageEnum) {
        return new SendMessage(chatId, messageEnum.getText());
    }

    private void saveLastCommand(@Nullable BotCommandEnum commandEnum, Long userId) {
        if (commandEnum != null && commandEnum != BotCommandEnum.HELP) {
            userService.saveLastCommand(userId, commandEnum.getCommandName());
        }
    }

    private void sendToAllUsers(Map<String, ?> chatIdMessageMap) {
        PinTelBot bot = context.getBean(PinTelBot.class);

        chatIdMessageMap
                .entrySet()
                .forEach(um -> {
                    String chatId = um.getKey();
                    Object message = um.getValue();
                    try {
                        bot.execute(new SendMessage(chatId, message.toString()));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                });
    }





}
