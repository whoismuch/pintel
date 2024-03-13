package com.pintel.controller;

import com.pintel.PinTelBot;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@AllArgsConstructor
public class WebhookController {
    PinTelBot bot;

    @PostMapping("/telegram/api")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return bot.onWebhookUpdateReceived(update);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
