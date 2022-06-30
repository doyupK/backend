package com.tutti.backend.dto.Feed;

import lombok.Getter;
import lombok.Setter;

// 피드 상세 조회 페이지 final response Dto
@Getter
@Setter
public class FeedResponseDto {
    int success;

    String message;

    FeedDetailResponseDto data;
}
