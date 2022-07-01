package com.tutti.backend.dto.user.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// 나 or 타인 유저 LikeList, UpLoadList 작성 Response
@Getter
@Setter
@NoArgsConstructor
public class UserDataResponseDto {
    int success;
    String message;
    List<UserPageFeedDtoMapping> data;
}
