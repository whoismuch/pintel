package com.pintel.service;

import com.pintel.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "tags-mean", url = "http://192.168.13.153:8000", path = "", configuration = FeignConfig.class)
public interface WatermarkClient {

    @PostMapping (value = "/img", headers = "multipart/form-data")
    String getImages(@RequestParam("file") byte[] file);
}
