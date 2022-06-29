package com.tutti.backend.dto.Feed;


import lombok.Getter;

import java.util.List;

@Getter
public class MainPageListDto {

    private final List<SearchTitleDtoMapping> lastestList;

    private final List<MainPageFeedDto> likeList;

    private final List<MainPageFeedDto> genreList;

    public MainPageListDto(List<SearchTitleDtoMapping> lastestList, List<MainPageFeedDto> likeList, List<MainPageFeedDto> randomList) {
        this.lastestList = lastestList;
        this.likeList = likeList;
        this.genreList = randomList;
    }
}
