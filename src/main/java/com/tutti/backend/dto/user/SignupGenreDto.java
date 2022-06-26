package com.tutti.backend.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupGenreDto {

    String favoriteGenre1;
    String favoriteGenre2;
    String favoriteGenre3;
    String favoriteGenre4;

    public SignupGenreDto(SignupRequestDto signupRequestDto) {
        this.favoriteGenre1 = signupRequestDto.genre[0];
        this.favoriteGenre2 = signupRequestDto.genre[1];
        this.favoriteGenre3 = signupRequestDto.genre[2];
        this.favoriteGenre4 = signupRequestDto.genre[3];

    }
}
