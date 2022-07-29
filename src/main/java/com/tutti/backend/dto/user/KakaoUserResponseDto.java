package com.tutti.backend.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KakaoUserResponseDto {
    private String JWtToken;
    private String artist;
/*    private boolean result;*/
    private String nowTime2;
}