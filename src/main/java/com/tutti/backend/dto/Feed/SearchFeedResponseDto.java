package com.tutti.backend.dto.Feed;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 피드 검색 response Dto
@Data
public class SearchFeedResponseDto {
    int success;
    String message;
    List<GetMainPageListDto> title;
    List<GetMainPageListDto> artist;
    List<GetMainPageListDto> video;
}
