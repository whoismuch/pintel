package com.pintel.service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

//Херня под названием феин клиент не могла передать файл в виде байтов,
// поэтому пришлось заюзать эту залупу
//если у кого то есть желание может переделать под феин клиент
@Deprecated
@Component
public class ImageMeaningTagsWithByteClient {

    @Value("${meaning-tags.url}")
    private String path;


    public String getMeaningTagsByPic(byte[] fileContents, final String filename) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(path);
        uri = uri.resolve("/img");
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

        map.add("name", filename);
        map.add("filename", filename);
        ByteArrayResource contentsAsResource = new ByteArrayResource(fileContents) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        map.add("file", contentsAsResource);

        return restTemplate.postForObject(uri, map, String.class);
    }
}


