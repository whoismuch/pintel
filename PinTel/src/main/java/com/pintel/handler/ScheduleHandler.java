package com.pintel.handler;

import com.pintel.constants.BotCommandEnum;
import com.pintel.service.TgUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@EnableScheduling
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ScheduleHandler {

    final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    final TgUserService userService;
    final MessageHandler messageHandler;


    @Scheduled(fixedRate = 60000) // каждую минуту
    public void sendScheduledMessage() {
        String messageText = "Пук пук";

        HashMap<String, Object> chatMessageMap = new HashMap<>();
        chatMessageMap.put("503041623", messageText);
        chatMessageMap.put("608338063", "Ха-ха лол треш");
        chatMessageMap.put("542156907", "Че за дичь пиппец");

        messageHandler.processCommand(BotCommandEnum.SEND_NEWSLETTER, chatMessageMap);
    }
}
