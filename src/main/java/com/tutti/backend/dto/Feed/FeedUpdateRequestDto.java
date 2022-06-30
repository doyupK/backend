package com.tutti.backend.dto.Feed;

import lombok.Getter;

// 피드 수정 RequestDto
@Getter
public class FeedUpdateRequestDto {

    private String title;

    private String description;

    private String color;


}
