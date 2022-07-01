package com.tutti.backend.dto.user.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

// 나 or 타인 유저 LikeList, UpLoadList 작성 Dto
public interface UserPageFeedDtoMapping {
        default Long getFeedId() { return getId(); }
        String getTitle();
        default String getArtist() { return getUserArtist(); }
        String getGenre();
        String getAlbumImageUrl();
        default String getProfileUrl() { return getUserProfileUrl(); }

        @JsonIgnore
        Long getId();
        @JsonIgnore
        String getUserArtist();
        @JsonIgnore
        String getUserProfileUrl();
}
