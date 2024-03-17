package com.pintel.controller;

import com.pintel.PinTelBot;
import com.pintel.service.ImageToMeaningTagsService;
import com.pintel.service.PinterestService;
import com.pintel.service.WatermarkModelService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class WebhookController {
    PinTelBot bot;
    PinterestService pinterestService;
    WatermarkModelService watermarkModelService;
    ImageToMeaningTagsService imageToMeaningTagsService;

    @PostMapping("/telegram/api")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return bot.onWebhookUpdateReceived(update);
    }


    @PostMapping("/test/tag")
    @SneakyThrows
    public Object getTagsFromPic(@RequestBody MultipartFile file) {
        byte[] fileBytes = file.getBytes();
        String tags = imageToMeaningTagsService.imageToTags(fileBytes);
        return pinterestService.getPictureByTag(tags);
    }


    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
