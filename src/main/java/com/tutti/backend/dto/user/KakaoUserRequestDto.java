package com.tutti.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserRequestDto {
    private Long id;
    private String nickname;
    private String profileUrl;
    private String email;
}
