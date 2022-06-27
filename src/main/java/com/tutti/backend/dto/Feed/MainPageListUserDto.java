package com.tutti.backend.dto.Feed;

import java.util.List;

public class MainPageListUserDto {

    private List<SearchTitleDtoMapping> lastestList;

    private List<MainPageFeedDto> likeList;

    private List<SearchTitleDtoMapping> interestedList;

    public MainPageListUserDto(List<SearchTitleDtoMapping> lastestList, List<MainPageFeedDto> likeList, List<SearchTitleDtoMapping> interestedList) {
        this.lastestList = lastestList;
        this.likeList = likeList;
        this.interestedList = interestedList;
    }
}
