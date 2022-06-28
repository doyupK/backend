package com.tutti.backend.dto.Feed;

import com.tutti.backend.dto.user.FollowingDtoMapping;
import com.tutti.backend.dto.user.response.UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserinfoResponseFeedDto {
    UserInfo userInfo;
    List<MainPageFeedDto> likeList;
    List<FollowingDtoMapping> followingList;

}
