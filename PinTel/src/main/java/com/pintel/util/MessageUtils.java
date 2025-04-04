package com.pintel.util;

import com.pintel.constants.BotMessageEnum;
import com.pintel.constants.ButtonTextEnum;
import com.pintel.keyboards.InlineKeyboardMaker;
import com.pintel.keyboards.ReplyKeyboardMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
public class MessageUtils {
    @Autowired
    InlineKeyboardMaker inlineKeyboardMaker;
    @Autowired
    ReplyKeyboardMaker replyKeyboardMaker;

    public SendMessage chooseSelectionType(String chatId) {
        List<String> types = List.of(BotMessageEnum.CHOOSE_TYPE_CONCEPT.getText(), BotMessageEnum.CHOOSE_TYPE_COLOR.getText());
        SendMessage sendMessage = getSendMessage(chatId, BotMessageEnum.CHOOSE_SELECTION_TYPE);
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getInlineMessageButtons(types));
        return sendMessage;
    }

    public SendMessage getSendMessage(String chatId, BotMessageEnum messageEnum) {
        return new SendMessage(chatId, messageEnum.getText());
    }

    public SendMessage getSendMessage(String chatId, String message) {
        return new SendMessage(chatId, message);
    }

    public SendMessage getSendMessageWithSelectionKeyboard(String chatId, BotMessageEnum messageEnum) {
        SendMessage sendMessage = getSendMessage(chatId, messageEnum);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getOneButtonKeyboard(ButtonTextEnum.SELECTION.getText()));
        return sendMessage;
    }

    public SendMessage getLoadImageMessage(String chatId, String selectionType) {
        return new SendMessage(chatId, BotMessageEnum.LOAD_IMAGE.getText() + " " + selectionType.toLowerCase());
    }

    public SendMessage getOnlyKeyboardSelectionMessage() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(replyKeyboardMaker.getOneButtonKeyboard(ButtonTextEnum.SELECTION.getText()));
        return sendMessage;
    }
}
