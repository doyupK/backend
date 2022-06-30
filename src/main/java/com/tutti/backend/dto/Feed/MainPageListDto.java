package com.tutti.backend.dto.Feed;


import lombok.Getter;

import java.util.List;

// 비 로그인 유저의 메인 페이지(최신 순, 좋아요 순, 랜덤) middleDto
@Getter
public class MainPageListDto {

    private final List<SearchTitleDtoMapping> lastestList;

    private final List<SearchTitleDtoMapping> likeList;

    private final List<MainPageFeedDto> genreList; // 랜덤

    public MainPageListDto(List<SearchTitleDtoMapping> lastestList, List<SearchTitleDtoMapping> likeList, List<MainPageFeedDto> randomList) {
        this.lastestList = lastestList;
        this.likeList = likeList;
        this.genreList = randomList;
    }
}
