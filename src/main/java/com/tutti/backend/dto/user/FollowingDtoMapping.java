package com.tutti.backend.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface FollowingDtoMapping {
    default String getprofileImage(){
        return getFollowingUserProfileUrl();
    }
    default Long getid(){
        return getFollowingUserId();
    }
    default String getartist() {
        return getFollowingUserArtist();
    }

    @JsonIgnore
    String getFollowingUserProfileUrl();
    @JsonIgnore
    Long getFollowingUserId();
    @JsonIgnore
    String getFollowingUserArtist();
}
