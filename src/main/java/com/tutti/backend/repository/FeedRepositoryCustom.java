package com.tutti.backend.repository;

import com.tutti.backend.dto.Feed.GetFeedByPostTypeDto;

import java.util.List;

public interface FeedRepositoryCustom {


List<GetFeedByPostTypeDto> getFeedByPostType(String postType,String genre);


}
