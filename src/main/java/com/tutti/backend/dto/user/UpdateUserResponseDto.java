package com.tutti.backend.dto.user;

import lombok.Data;

@Data
public class UpdateUserResponseDto {

    private int success;
    private String message;

    private UpdateUserResponseDataDto data;

    public UpdateUserResponseDto(int success, String message, UpdateUserResponseDataDto data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
