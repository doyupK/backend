package com.tutti.backend.dto.Feed;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedAllDto {

    int success;

    String message;

    MainPageListDto data;
}
