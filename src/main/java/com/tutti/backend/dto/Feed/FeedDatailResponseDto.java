package com.tutti.backend.dto.Feed;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedDatailResponseDto {
    int success;

    String message;

    FeedDetailResponseDto data;
}
