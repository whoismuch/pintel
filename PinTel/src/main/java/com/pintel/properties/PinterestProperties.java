package com.pintel.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pinterest")
@Data
public class PinterestProperties {

    private String link;
    private String apiKey;
    private String engine;
}
