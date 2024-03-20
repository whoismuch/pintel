package com.pintel.handler;

import com.pintel.constants.BotMessageEnum;
import com.pintel.service.TgUserService;
import com.pintel.util.MessageUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallbackQueryHandler {
    final TgUserService userService;
    final MessageUtils messageUtils;
    final List<String> selectionTypes = List.of(BotMessageEnum.CHOOSE_TYPE_CONCEPT.getText(),
            BotMessageEnum.CHOOSE_TYPE_COLOR.getText());

    public CallbackQueryHandler(TgUserService userService, MessageUtils messageUtils) {
        this.userService = userService;
        this.messageUtils = messageUtils;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        Long userId = callbackQuery.getFrom().getId();
        String inputText = callbackQuery.getData();

        if (inputText != null && selectionTypes.contains(inputText)) {
            userService.saveSelectionType(userId, inputText);
            return messageUtils.getLoadImageMessage(chatId, userService.getSelectionType(userId));
        }
        return messageUtils.chooseSelectionType(chatId);
    }
}
