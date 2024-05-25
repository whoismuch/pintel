package com.pintel.handler;

import com.pintel.PinTelBot;
import com.pintel.constants.BotCommandEnum;
import com.pintel.constants.BotMessageEnum;
import com.pintel.constants.ButtonTextEnum;
import com.pintel.exception.CommandNotFoundException;
import com.pintel.properties.TelegramProperties;
import com.pintel.service.ImageToColorTagsService;
import com.pintel.service.ImageToMeaningTagsService;
import com.pintel.service.PinterestService;
import com.pintel.service.TgUserService;
import com.pintel.service.client.ImageByteTelegramClient;
import com.pintel.service.client.ImageColorTagsClient;
import com.pintel.service.client.ImageMeaningTagsWithByteClient;
import com.pintel.service.client.ImageMeaningTagsWithLinkClient;
import com.pintel.util.MessageUtils;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {
    final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    final TgUserService userService;
    final MessageUtils messageUtils;
    final ApplicationContext context;
    final TelegramProperties telegramProperties;
    final ImageToMeaningTagsService imageToMeaningTagsService;
    final PinterestService pinterestService;
    final ImageToColorTagsService imageToColorTagsService;

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SendMessage processCommand(BotCommandEnum commandEnum, Long userId, String chatId) {
        SendMessage message = switch (commandEnum) {
            case START -> {
                userService.addUser(userId, null, chatId, BotCommandEnum.START.getCommandName());
                yield messageUtils.getSendMessage(chatId, BotMessageEnum.HELP_MESSAGE);
            }
            case HELP -> new SendMessage(chatId, BotMessageEnum.HELP_MESSAGE.getText());
            case MAKE_SELECTION -> messageUtils.chooseSelectionType(chatId);
            default -> null;
        };
        saveLastCommand(commandEnum, userId);
        return message;
    }

    public void processCommand(BotCommandEnum commandEnum, Map<String, ?> chatMessageMap) {
        if (Objects.requireNonNull(commandEnum) == BotCommandEnum.SEND_NEWSLETTER) {
            sendToAllUsers(chatMessageMap);
        }
    }

    private SendMessage processMakeSelection(PinTelBot bot, Message message, String inputText, Long userId, String chatId) throws TelegramApiException, IOException {
        try {
            if (inputText != null && userService.getSelectionType(userId) == null) {
                return messageUtils.chooseSelectionType(chatId);
            } else if (message.hasPhoto()) {
                List<PhotoSize> photos = message.getPhoto();

                String filePath = bot.execute(new GetFile(photos.get(photos.size() - 1).getFileId())).getFilePath();
                String urlFilePath = telegramProperties.getApiUrl() + "file/bot" + telegramProperties.getBotToken() + "/" + filePath;

                logger.info("url from user " + urlFilePath);
                List<String> tags = List.of();
                if (userService.getSelectionType(userId).equals(ButtonTextEnum.SELECTION_BY_COLOR.getText())) {
                    tags = imageToColorTagsService.imageToTagsColor(urlFilePath).getTags();
                    //   tags = List.of("krosh");
                    logger.info(tags.toString());
                } else {
                    //tags = List.of("kopatich");
                    tags = imageToMeaningTagsService.imageToTags(urlFilePath).getTags();
                    logger.info(tags.toString());
                }
                if (tags.isEmpty()) {
                    return messageUtils.getSendMessage(chatId, "Простите, я не обучен такое распозновать, попробуйте другое изображение");
                }

                var picLink = pinterestService.getPictureLinkByTag(String.join(" ", tags));
                SendPhoto sendPhoto = SendPhoto.builder()
                        .chatId(chatId)
                        .photo(new InputFile(picLink))
                        .caption(BotMessageEnum.RESULT_SELECTION.getText())
                        .build();
                bot.execute(sendPhoto);
                userService.saveSelectionType(userId, null);
                return new SendMessage();
            }
            return messageUtils.getLoadImageMessage(chatId, userService.getSelectionType(userId));
        } catch (Exception e) {
            logger.error("Error message: " + e.getMessage());
            return messageUtils.getSendMessage(chatId, BotMessageEnum.EXCEPTION_ILLEGAL_MESSAGE);
        }
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
                .forEach((chatId, message) -> {
                    try {
                        bot.execute(new SendMessage(chatId, message.toString()));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static <T> List<T> getFirstThreeElements(List<T> list) {
        int size = list.size();
        if (size < 3) {
            return new ArrayList<>(list);
        } else {
            int endIndex = Math.min(list.size(), 3);
            return list.subList(0, endIndex);
        }
    }
}
