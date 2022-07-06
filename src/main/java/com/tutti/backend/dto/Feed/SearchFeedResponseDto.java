package com.tutti.backend.dto.Feed;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 피드 검색 response Dto
@Getter
@Setter
public class SearchFeedResponseDto {
    int success;
    String message;
    List<SearchTitleDtoMapping> title;
    List<SearchArtistDtoMapping> artist;
    List<SearchTitleDtoMapping> video;
}
