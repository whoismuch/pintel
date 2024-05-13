package com.pintel.service.client;

import com.pintel.config.FeignConfig;
import com.pintel.dto.SearchResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "meaning-tag", url = "${meaning-tags.url}", path = "", configuration = FeignConfig.class)
public interface ImageMeaningTagsWithLinkClient {
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    List<String> getMeaningTagsByPic(@RequestParam(name = "link") String link);
}
