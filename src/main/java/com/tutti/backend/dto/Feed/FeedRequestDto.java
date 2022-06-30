package com.tutti.backend.dto.Feed;

import lombok.Getter;

// 피드 작성 RequestDto
@Getter
public class FeedRequestDto {


    private String title;

    private String description;

    private String genre;

    private String postType;

    private String color;


}
