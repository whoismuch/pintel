package com.pintel.service.client;

import com.pintel.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "color-tag", url = "${color-tags.url}", path = "", configuration = FeignConfig.class)
public interface ImageColorTagsClient {
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    List<String> getColorTagsByPic(@RequestParam(name = "link") String link);
}
