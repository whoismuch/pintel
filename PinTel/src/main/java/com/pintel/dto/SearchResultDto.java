package com.pintel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchResultDto {
    @JsonProperty("images_results")
    private List<ImageResultDto> imagesResults;
}
