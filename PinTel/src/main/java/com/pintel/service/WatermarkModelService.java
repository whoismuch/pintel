package com.pintel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class WatermarkModelService {

    private final WatermarkClient watermarkClient;

    public Object watermarkHandler(byte[] file) {

        return watermarkClient.getImages(file);
    }
}
