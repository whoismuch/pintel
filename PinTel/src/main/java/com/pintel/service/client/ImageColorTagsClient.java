package com.pintel.service.client;

import com.pintel.config.FeignConfig;
import com.pintel.dto.TagsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "color-tag", url = "${model-tags.url}", path = "", configuration = FeignConfig.class)
public interface ImageColorTagsClient {
    @GetMapping(value = "/tags/color", produces = MediaType.APPLICATION_JSON_VALUE)
    TagsResponseDto getColorTagsByPic(@RequestParam(name = "link") String link);
}
