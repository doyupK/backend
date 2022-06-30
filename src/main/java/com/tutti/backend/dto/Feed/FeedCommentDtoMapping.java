package com.tutti.backend.dto.Feed;

import com.fasterxml.jackson.annotation.JsonIgnore;

// 코멘트 매핑Dto
public interface FeedCommentDtoMapping {
    Long getId();
    String getComment();
    default String getProfileUrl(){
        return getUserProfileUrl();
    }
    default String getArtist(){
        return getUserArtist();
    }
    @JsonIgnore
    String getUserProfileUrl();
    @JsonIgnore
    String getUserArtist();

}
