package com.tutti.backend.dto.Feed;

import lombok.Getter;
import lombok.Setter;


// 비 로그인 유저의 메인 페이지(최신 순, 좋아요 순, 랜덤) final ResponseDto
@Getter
@Setter
public class FeedMainNotLoginResponseDto {

    int success;

    String message;

    MainPageListDto data;
}
