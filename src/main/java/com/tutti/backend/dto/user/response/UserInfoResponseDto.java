package com.tutti.backend.dto.user.response;

import java.util.List;

public class UserInfoResponseDto {
    int success;
    String message;
    List<String> userInfo;
    List<String> likeList;
    List<String> followingList;
    List<String> playList;
}
