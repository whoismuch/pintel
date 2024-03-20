package com.pintel.handler;

import com.pintel.constants.BotCommandEnum;
import com.pintel.model.TgUser;
import com.pintel.service.PinterestService;
import com.pintel.service.TgUserService;
import com.pintel.service.UserTagService;
import javassist.bytecode.ByteArray;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ScheduleHandler {

    final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    final TgUserService userService;
    final UserTagService userTagService;
    final MessageHandler messageHandler;
    final PinterestService pinterestService;


    @Scheduled(cron = "0 0 8 * * *")
    public void sendScheduledMessage() throws IOException {

        List<TgUser> tgUsers = userService.getTgUsersByUsersId(userTagService.getDistinctUsers());
        Map<Long, List<String>> userPopularTags = userTagService.getMostPopularTagsForUsers();

        Map<String, List<String>> chatPopularTags = tgUsers
                .stream()
                .collect(HashMap::new,
                        (m, tU) -> m.put(tU.getChatId(), userPopularTags.get(tU.getUserId())), HashMap::putAll);

        Map<String, List<byte[]>> chatRecommendedImage = chatPopularTags
                .entrySet()
                .stream()
                .collect(HashMap::new,
                        (m, cT) -> m.put(cT.getKey(), cT.getValue()
                                .stream()
                                .map(pinterestService::getPictureByTag)
                                .collect(Collectors.toList())), HashMap::putAll);

        messageHandler.processCommand(BotCommandEnum.SEND_NEWSLETTER, chatRecommendedImage);
    }
}

