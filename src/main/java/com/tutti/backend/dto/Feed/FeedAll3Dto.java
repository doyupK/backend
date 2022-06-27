package com.tutti.backend.dto.Feed;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedAll3Dto {
    int success;

    String message;

    FeedDetailResponseDto data;
}
