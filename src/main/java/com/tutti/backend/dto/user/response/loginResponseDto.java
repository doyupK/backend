package com.tutti.backend.dto.user.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class loginResponseDto {
    int success;
    String message;
    String artist;
    String profileUrl;
    String expiredTime;
}
