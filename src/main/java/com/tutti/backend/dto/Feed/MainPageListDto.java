package com.tutti.backend.dto.Feed;


import lombok.Getter;

import java.util.List;

@Getter
public class MainPageListDto {

    private List<MainPageFeedDto> lastestList;

    private List<MainPageFeedDto> likeList;

    private List<MainPageFeedDto> randomList;
}
