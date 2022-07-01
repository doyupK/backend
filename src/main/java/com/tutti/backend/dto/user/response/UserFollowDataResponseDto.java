package com.tutti.backend.dto.user.response;

import com.tutti.backend.dto.user.FollowingDtoMapping;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// 나 or 타인 유저 FollowList 작성 Response
@Getter
@Setter
@NoArgsConstructor
public class UserFollowDataResponseDto {
    int success;
    String message;
    List<FollowingDtoMapping> data;
}
