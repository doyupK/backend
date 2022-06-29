package com.tutti.backend.dto.Feed;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedMainLoginResponseDto {

    int success;

    String message;

    MainPageListUserDto data;
}
