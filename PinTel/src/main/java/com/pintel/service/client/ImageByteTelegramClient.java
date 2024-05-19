package com.pintel.service.client;

import com.pintel.dto.ImageResultDto;
import com.pintel.dto.SearchResultDto;
import com.pintel.properties.PinterestProperties;
import com.pintel.service.PinterestService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

@Deprecated
@Component
public class ImageByteTelegramClient {

    public byte[] getPictureByTag(String path) throws IOException {
        URL url = new URL(path);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return getPicFromLink(connection);

        }
        return null;
    }

    private byte[] getPicFromLink(HttpURLConnection connection) throws IOException {

        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        return outputStream.toByteArray();
    }
}




