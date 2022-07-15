package com.tutti.backend.dto.Feed;

import lombok.Getter;

import java.util.List;

// 로그인한 유저의 메인 페이지(최신 순, 좋아요 순, 관심장르) middleDto
@Getter
public class MainPageListUserDto {

    private final List<GetMainPageListDto> latestList;

    private final List<SearchTitleDtoMapping> likeList;

    private final List<GetMainPageListDto> genreList;

    private final List<GetMainPageListDto> videoList;

    public MainPageListUserDto(List<GetMainPageListDto> latestList, List<SearchTitleDtoMapping> likeList, List<GetMainPageListDto> interestedList,List<GetMainPageListDto> videoList) {
        this.latestList = latestList;
        this.likeList = likeList;
        this.genreList = interestedList;
        this.videoList=videoList;
    }
}
