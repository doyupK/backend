package com.tutti.backend.dto.user;

import lombok.Data;

@Data
public class UpdateUserResponseDataDto {

    private String artist;

    private String profileImage;

    public UpdateUserResponseDataDto(String artist, String profileImage) {
        this.artist = artist;
        this.profileImage = profileImage;
    }
}
