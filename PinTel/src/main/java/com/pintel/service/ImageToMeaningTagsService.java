package com.pintel.service;

import com.pintel.exception.AnotherServiceException;
import com.pintel.service.client.ImageMeaningTagsClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.net.URISyntaxException;


@RequiredArgsConstructor
@Service
public class ImageToMeaningTagsService {

    private final ImageMeaningTagsClient imageMeaningTagsClient;
    private Logger logger = LoggerFactory.getLogger(PinterestService.class);

    public String imageToTags(byte[] file) throws AnotherServiceException {
        try {
            String tags = imageMeaningTagsClient.getMeaningTagsByPic(file, "file");
            logger.info("tags received: " + tags);
            return tags;
        } catch (RestClientException | URISyntaxException e){
           logger.error(e.getMessage());
           throw new AnotherServiceException(e.getMessage());
        }
    }
}
