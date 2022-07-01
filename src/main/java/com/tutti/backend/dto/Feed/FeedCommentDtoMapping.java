package com.tutti.backend.dto.Feed;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

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
    String getModifiedAt();
    @JsonIgnore
    String getUserProfileUrl();
    @JsonIgnore
    String getUserArtist();

}
