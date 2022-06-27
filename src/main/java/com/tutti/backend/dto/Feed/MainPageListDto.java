package com.tutti.backend.dto.Feed;


import lombok.Getter;

import java.util.List;

@Getter
public class MainPageListDto {

    private List<SearchTitleDtoMapping> lastestList;

    private List<SearchTitleDtoMapping> likeList;

    private List<SearchTitleDtoMapping> randomList;

    public MainPageListDto(List<SearchTitleDtoMapping> lastestList, List<SearchTitleDtoMapping> likeList, List<SearchTitleDtoMapping> randomList) {
        this.lastestList = lastestList;
        this.likeList = likeList;
        this.randomList = randomList;
    }
}
