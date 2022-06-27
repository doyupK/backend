package com.tutti.backend.dto.Feed;

import java.util.List;

public class MainPageListUserDto {

    private List<SearchTitleDtoMapping> lastestList;

    private List<SearchTitleDtoMapping> likeList;

    private List<SearchTitleDtoMapping> interestedList;

    public MainPageListUserDto(List<SearchTitleDtoMapping> lastestList, List<SearchTitleDtoMapping> likeList, List<SearchTitleDtoMapping> interestedList) {
        this.lastestList = lastestList;
        this.likeList = likeList;
        this.interestedList = interestedList;
    }
}
