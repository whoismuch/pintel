package com.pintel.service;

import com.pintel.dto.TagsResponseDto;
import com.pintel.exception.AnotherServiceException;
import com.pintel.service.client.ImageColorTagsClient;
import com.pintel.service.client.ImageMeaningTagsWithLinkClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;


@RequiredArgsConstructor
@Service
public class ImageToColorTagsService {

    private final ImageColorTagsClient imageColorTagsClient;
    private Logger logger = LoggerFactory.getLogger(PinterestService.class);

    public TagsResponseDto imageToTagsColor(String url) throws AnotherServiceException {
        try {
            var tags = imageColorTagsClient.getColorTagsByPic(url);
            logger.info("tags received: " + tags);
            return tags;
        } catch (RestClientException e){
           logger.error(e.getMessage());
           throw new AnotherServiceException(e.getMessage());
        }
    }
}
