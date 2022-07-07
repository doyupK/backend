package com.tutti.backend.repository;

import com.tutti.backend.dto.Feed.GetFeedByPostTypeDto;
import com.tutti.backend.dto.Feed.GetMainPageListDto;

import java.util.List;

public interface FeedRepositoryCustom {


List<GetFeedByPostTypeDto> getFeedByPostType(String postType,String genre);

List<GetMainPageListDto> getMainPageRandomList(String audio);

List<GetMainPageListDto> searchMusicByTitleKeyword(String keyword); // 12

List<GetMainPageListDto> searchMusicByArtistKeyword(String keyword); // 12

List<GetMainPageListDto> searchVideoByTitleKeyword(String keyword); // 8
    List<GetMainPageListDto> searchCategoryByKeyword(String category, String keyword);



}
