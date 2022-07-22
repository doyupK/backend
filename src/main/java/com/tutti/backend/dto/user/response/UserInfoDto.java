package com.tutti.backend.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {
    String artist;
    String[] genre;
    boolean[] genreSelected;
    String profileImage;
    Long followerCount;
    Long followingCount;
    String profileText;
    String instagramUrl;
    String youtubeUrl;

}
