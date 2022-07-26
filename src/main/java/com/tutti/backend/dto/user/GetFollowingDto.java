package com.tutti.backend.dto.user;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class GetFollowingDto {

    private String profileImage;

    private Long id;

    private String artist;


    @QueryProjection
    public GetFollowingDto(String profileImage, Long id, String artist) {
        this.id = id;
        this.artist = artist;
        this.profileImage = profileImage;
    }

}
