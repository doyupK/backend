package com.tutti.backend.dto.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tutti.backend.dto.Feed.UserinfoResponseFeedDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponseDto {
    int success;
    String message;
    Boolean isFollow;
    UserinfoResponseFeedDto data;
}
