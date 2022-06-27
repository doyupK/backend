package com.tutti.backend.dto.google;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GoogleUserInfoDto {
    String email;
    String nickname;
    String profileImage;
}
