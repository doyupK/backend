package com.tutti.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LiveChannelResponseDto {
    private String artist;
    private String title;
    private String profileUrl;
    private String thumbNailImageUrl;
}