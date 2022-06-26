package com.tutti.backend.dto.user;

import lombok.Data;

import javax.validation.Valid;


@Data
public class UserInfoRequestDto {
    @Valid
    SignupRequestDto userInfo;
}
