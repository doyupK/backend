package com.tutti.backend.dto.Feed;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 피드 전체 페이지 response Dto
@Getter
@Setter
public class FeedPageResponseDto {

    int success;
    String message;
    List<SearchTitleDtoMapping> data;
}
