package com.pintel.config;

import com.pintel.PinTelBot;
import com.pintel.handler.CallbackQueryHandler;
import com.pintel.handler.MessageHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
@AllArgsConstructor
public class SpringConfig {
    private final TelegramConfig telegramConfig;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(telegramConfig.getWebhookPath()).build();
    }

    @Bean
    public PinTelBot springWebhookBot(SetWebhook setWebhook,
                                      MessageHandler messageHandler,
                                      CallbackQueryHandler callbackQueryHandler) {
        PinTelBot bot = new PinTelBot(setWebhook, messageHandler, callbackQueryHandler);

        bot.setBotPath(telegramConfig.getWebhookPath());
        bot.setBotUsername(telegramConfig.getBotName());
        bot.setBotToken(telegramConfig.getBotToken());

        return bot;
    }
}
