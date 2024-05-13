package com.pintel.config;

import com.pintel.PinTelBot;
import com.pintel.handler.CallbackQueryHandler;
import com.pintel.handler.MessageHandler;
import com.pintel.properties.TelegramProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
@AllArgsConstructor
public class SpringConfig {
    private final TelegramProperties telegramProperties;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(telegramProperties.getWebhookPath()).build();
    }

    @Bean
    public PinTelBot springWebhookBot(SetWebhook setWebhook,
                                      MessageHandler messageHandler,
                                      CallbackQueryHandler callbackQueryHandler) {
        PinTelBot bot = new PinTelBot(setWebhook, messageHandler, callbackQueryHandler);

        bot.setBotPath(telegramProperties.getWebhookPath());
        bot.setBotUsername(telegramProperties.getBotName());
        bot.setBotToken(telegramProperties.getBotToken());

        return bot;
    }
}
