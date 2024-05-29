package com.pintel.service;

import com.pintel.dto.ImageResultDto;
import com.pintel.dto.SearchResultDto;
import com.pintel.properties.PinterestProperties;
import com.pintel.service.client.PinterestClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PinterestService {

    private Logger logger = LoggerFactory.getLogger(PinterestService.class);


    private final PinterestClient pinterestClient;

    private final PinterestProperties pinterestProperties;

    private HttpURLConnection urlConnectionOkPic;

    public String getPictureLinkByTag(String world) {
        SearchResultDto response = pinterestClient.getImages(pinterestProperties.getApiKey(),
                pinterestProperties.getEngine(), world);

        logger.info(response.toString());

        return getValidUrlPic(response);
    }

    @SneakyThrows
    private String getValidUrlPic(SearchResultDto response) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < response.getImagesResults().size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        String resultUrl = "";
        for (int index : indices) {
            ImageResultDto imageResult = response.getImagesResults().get(index);

            try {
                URL url = new URL(imageResult.getOriginal());
                if (!(url.toString().endsWith(".jpg") || url.toString().endsWith(".jpeg") || url.toString().endsWith(".png"))) {
                    continue;
                }
                logger.info("url from yandex " + imageResult.getOriginal());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(6000);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    logger.info("Изображение доступно");
                    resultUrl = url.toString();
                    urlConnectionOkPic = connection;
                    return resultUrl;

                } else {
                    logger.info("Изображение недоступно");
                }
            } catch (Exception e) {
                logger.error("Ошибка при загрузке изображения: " + e.getMessage());
                getValidUrlPic(response);
            }
        }
        return resultUrl;
    }

    @SneakyThrows
    public byte[] getPictureBytesByTags(String words) {
        getPictureLinkByTag(words);
        InputStream inputStream = urlConnectionOkPic.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        byte[] imageBytes = outputStream.toByteArray();
        logger.info("Изображение успешно получено");

        return imageBytes;
    }
}
