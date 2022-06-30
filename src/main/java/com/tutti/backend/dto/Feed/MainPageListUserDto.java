package com.tutti.backend.dto.Feed;

import lombok.Getter;

import java.util.List;

// 로그인한 유저의 메인 페이지(최신 순, 좋아요 순, 관심장르) middleDto
@Getter
public class MainPageListUserDto {

    private final List<SearchTitleDtoMapping> lastestList;

    private final List<MainPageFeedDto> likeList;

    private final List<SearchTitleDtoMapping> genreList;

    public MainPageListUserDto(List<SearchTitleDtoMapping> lastestList, List<MainPageFeedDto> likeList, List<SearchTitleDtoMapping> interestedList) {
        this.lastestList = lastestList;
        this.likeList = likeList;
        this.genreList = interestedList;
    }
}
