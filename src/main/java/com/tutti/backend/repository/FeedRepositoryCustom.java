package com.tutti.backend.repository;

import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.GetFeedByPostTypeDto;
import com.tutti.backend.dto.Feed.GetMainPageListDto;
import com.tutti.backend.dto.Feed.GetUserPageListDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface FeedRepositoryCustom {


List<GetFeedByPostTypeDto> getFeedByPostType(String postType,String genre);

Slice<GetFeedByPostTypeDto> getFeedByPostTypeInfiniteScroll(String postType, String genre, Pageable pageable);

List<GetMainPageListDto> getMainPageRandomList(String audio,String recommend);

List<GetMainPageListDto> searchMusicByTitleKeyword(String keyword); // 6

List<GetMainPageListDto> searchMusicByArtistKeyword(String keyword); // 6

List<GetMainPageListDto> searchVideoByTitleKeyword(String keyword); // 4
    List<GetMainPageListDto> searchVideoByArtistKeyword(String keyword); // 4
    List<GetMainPageListDto> searchCategoryByKeyword(String category, String keyword);

    List<GetMainPageListDto>  getMainPageLatestList(String audio);

    List<GetMainPageListDto> getMainPageVideoList(String video);

    List<GetMainPageListDto> getMainPageLoginGenreList(String audio, String genre);

    List<GetMainPageListDto> getMainPagLikeList();

    List<GetUserPageListDto> getTop6ByPostTypeAndHearts_UserAndHearts_IsHeartTrueOrderByHearts_IdDesc(String postType, User user);

    List<GetUserPageListDto>getTop6ByPostTypeAndHearts_UserAndHearts_IsHeartTrueOrderByHearts_IdDesc2(String postType, User user);

    List<GetUserPageListDto>getTop6ByPostTypeAndUserOrderByIdDesc(String postType,User user);

    List<GetUserPageListDto>getTop6ByPostTypeAndUserOrderByIdDesc2(String postType,User user);




}
