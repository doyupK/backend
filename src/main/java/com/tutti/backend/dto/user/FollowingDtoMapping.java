package com.tutti.backend.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface FollowingDtoMapping {
    default String getProfileImage(){
        return getFollowingUserProfileUrl();
    }
    default Long getId(){
        return getFollowingUserId();
    }
    default String getArtist() {
        return getFollowingUserArtist();
    }

    @JsonIgnore
    String getFollowingUserProfileUrl();
    @JsonIgnore
    Long getFollowingUserId();
    @JsonIgnore
    String getFollowingUserArtist();
}
