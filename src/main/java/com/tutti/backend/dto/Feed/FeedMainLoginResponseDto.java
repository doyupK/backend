package com.tutti.backend.dto.Feed;

import lombok.Getter;
import lombok.Setter;

// 로그인 유저의 메인 페이지(최신 순, 좋아요 순, 관심장르) final ResponseDto
@Getter
@Setter
public class FeedMainLoginResponseDto {

    int success;

    String message;

    MainPageListUserDto data;
}
