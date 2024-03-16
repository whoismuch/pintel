package com.pintel.controller;

import com.pintel.PinTelBot;
import com.pintel.service.PinterestService;
import com.pintel.service.WatermarkModelService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class WebhookController {
    PinTelBot bot;
    PinterestService pinterestService;
    WatermarkModelService watermarkModelService;

    @PostMapping("/telegram/api")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return bot.onWebhookUpdateReceived(update);
    }


    @PostMapping("/test/pinterest")
    public Object getPic(@RequestParam(name = "word") String word) throws IOException {
       return watermarkModelService.watermarkHandler(pinterestService.getPictureByTag(word));
    }


    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
