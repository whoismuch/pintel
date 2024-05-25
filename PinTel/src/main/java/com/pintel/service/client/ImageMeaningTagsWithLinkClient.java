package com.pintel.service.client;

import com.pintel.config.FeignConfig;
import com.pintel.dto.TagsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "meaning-tag", url = "${model-tags.url}", path = "", configuration = FeignConfig.class)
public interface ImageMeaningTagsWithLinkClient {
    @GetMapping(value = "/tags/content", produces = MediaType.APPLICATION_JSON_VALUE)
    TagsResponseDto getMeaningTagsByPic(@RequestParam(name = "link") String link);
}
