package com.pintel.service;

import com.pintel.config.FeignConfig;
import com.pintel.dto.ImageResultDto;
import com.pintel.dto.SearchResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "pinterest-url", url = "${pinterest.link}", path = "", configuration = FeignConfig.class)
public interface PinterestClient {
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    SearchResultDto getImages(@RequestParam(name = "api_key") String apiKey,
                              @RequestParam(name = "engine") String engine,
                                    @RequestParam(name = "text") String text);
}
