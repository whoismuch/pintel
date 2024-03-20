package com.pintel.service;

import com.pintel.dto.ImageResultDto;
import com.pintel.dto.SearchResultDto;
import com.pintel.properties.PinterestProperties;
import com.pintel.service.client.PinterestClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@Service
@RequiredArgsConstructor
public class PinterestService {

    private Logger logger = LoggerFactory.getLogger(PinterestService.class);


    private final PinterestClient pinterestClient;

    private final PinterestProperties pinterestProperties;

    public byte[] getPictureByTag(String world) {

        SearchResultDto response = pinterestClient.getImages(pinterestProperties.getApiKey(),
                pinterestProperties.getEngine(), world);
        logger.info(response.toString());
        byte[] resultPic = null;

        for (ImageResultDto imageResult : response.getImagesResults()) {
            try {
                URL url = new URL(imageResult.getOriginal());
                if (!(url.toString().endsWith(".jpg") || url.toString().endsWith(".jpeg") || url.toString().endsWith(".png"))) {
                    continue;
                }
                logger.info(imageResult.getLink());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    logger.info("Изображение доступно");
                    var pic = getPicFromLink(connection);
                    if (pic.length != 0) {
                        resultPic = pic;
                        break;
                    }
                } else {
                    logger.info("Изображение недоступно");
                }
            } catch (IOException e) {
                logger.error("Ошибка при загрузке изображения: " + e.getMessage());
            }
        }
        return resultPic;
    }

    private byte[] getPicFromLink(HttpURLConnection connection) throws IOException {

        InputStream inputStream = connection.getInputStream();
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
