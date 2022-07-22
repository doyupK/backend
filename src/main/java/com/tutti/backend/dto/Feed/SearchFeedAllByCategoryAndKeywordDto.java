package com.tutti.backend.dto.Feed;

import lombok.Data;

import java.util.List;

@Data
public class SearchFeedAllByCategoryAndKeywordDto {
    int success;
    String message;
    List<GetMainPageListDto> results;
}
