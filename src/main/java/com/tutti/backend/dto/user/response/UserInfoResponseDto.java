package com.tutti.backend.dto.user.response;

import com.tutti.backend.dto.Feed.MainPageFeedDto;
import com.tutti.backend.dto.Feed.UserinfoResponseFeedDto;
import com.tutti.backend.dto.user.FollowingDtoMapping;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoResponseDto {
    int success;
    String message;
    UserinfoResponseFeedDto data;
}
