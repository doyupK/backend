package com.tutti.backend.dto.Feed;


import lombok.Data;
import lombok.Getter;

import java.util.List;

// 비 로그인 유저의 메인 페이지(최신 순, 좋아요 순, 랜덤) middleDto
@Data
public class MainPageListDto {

    private final List<GetMainPageListDto> latestList;

    private final List<SearchTitleDtoMapping> likeList;

    private final List<GetMainPageListDto> genreList; // 랜덤
    private final List<GetMainPageListDto> videoList;

    public MainPageListDto(List<GetMainPageListDto> latestList, List<SearchTitleDtoMapping> likeList, List<GetMainPageListDto> randomList,List<GetMainPageListDto> videoLIst) {
        this.latestList = latestList;
        this.likeList = likeList;
        this.genreList = randomList;
        this.videoList = videoLIst;

    }


}
