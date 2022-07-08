package com.tutti.backend.dto.user;


import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Valid
public class SignupRequestDto {


    @NotBlank
    @Email(message = "Email 형식이 아닙니다.")
    String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 8자 이상입니다.")
    String password;

    String artist;

    String[] genre;

    String profileText;

    String instagramUrl;

    String youtubeUrl;

    boolean[] genreSelected;

}

