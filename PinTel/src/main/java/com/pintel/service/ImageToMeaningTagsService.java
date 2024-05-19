package com.pintel.service;

import com.pintel.dto.TagsResponseDto;
import com.pintel.exception.AnotherServiceException;
import com.pintel.service.client.ImageMeaningTagsWithByteClient;
import com.pintel.service.client.ImageMeaningTagsWithLinkClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.net.URISyntaxException;


@RequiredArgsConstructor
@Service
public class ImageToMeaningTagsService {

    private final ImageMeaningTagsWithLinkClient imageMeaningTagsWithLinkClient;
    private Logger logger = LoggerFactory.getLogger(PinterestService.class);

    public TagsResponseDto imageToTags(String url) throws AnotherServiceException {
        try {
            var tags = imageMeaningTagsWithLinkClient.getMeaningTagsByPic(url);
            logger.info("tags received: " + tags);
            return tags;
        } catch (RestClientException e){
           logger.error(e.getMessage());
           throw new AnotherServiceException(e.getMessage());
        }
    }
}
