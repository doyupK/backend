package com.tutti.backend.dto.Feed;

import lombok.Getter;

import java.util.List;

@Getter
public class MainPageListUserDto {

    private final List<SearchTitleDtoMapping> lastestList;

    private final List<MainPageFeedDto> likeList;

    private final List<SearchTitleDtoMapping> interestedList;

    public MainPageListUserDto(List<SearchTitleDtoMapping> lastestList, List<MainPageFeedDto> likeList, List<SearchTitleDtoMapping> interestedList) {
        this.lastestList = lastestList;
        this.likeList = likeList;
        this.interestedList = interestedList;
    }
}
