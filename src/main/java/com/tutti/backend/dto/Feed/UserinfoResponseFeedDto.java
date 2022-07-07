package com.tutti.backend.dto.Feed;

import com.tutti.backend.dto.user.FollowingDtoMapping;
import com.tutti.backend.dto.user.response.UserInfoDto;
import com.tutti.backend.dto.user.response.UserPageFeedDtoMapping;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserinfoResponseFeedDto {
    UserInfoDto userInfoDto;
    List<UserPageFeedDtoMapping> likeList;
    List<UserPageFeedDtoMapping> likeVideoList;
    List<FollowingDtoMapping> followingList;
    List<UserPageFeedDtoMapping> uploadList;
    List<UserPageFeedDtoMapping> uploadVideoList;

}
