package com.tutti.backend.repository;

import com.tutti.backend.dto.Feed.GetFeedByPostTypeDto;
import com.tutti.backend.dto.Feed.GetMainPageListDto;

import java.util.List;

public interface FeedRepositoryCustom {


List<GetFeedByPostTypeDto> getFeedByPostType(String postType,String genre);

List<GetMainPageListDto> getMainPageRandomList(String audio);

List<GetMainPageListDto> searchMusicByTitleKeyword(String keyword); // 6

List<GetMainPageListDto> searchMusicByArtistKeyword(String keyword); // 6

List<GetMainPageListDto> searchVideoByTitleKeyword(String keyword); // 4
    List<GetMainPageListDto> searchVideoByArtistKeyword(String keyword); // 4
    List<GetMainPageListDto> searchCategoryByKeyword(String category, String keyword);

    List<GetMainPageListDto>  getMainPageLatestList(String audio);

    List<GetMainPageListDto> getMainPageVideoList(String video);

    List<GetMainPageListDto> getMainPageLoginGenreList(String genre);




}
