package com.tutti.backend.dto.Feed;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class GetArtistListDto {

    private Long id;

    private String artist;

    private String profileUrl;

    @QueryProjection
    public GetArtistListDto(Long id, String artist, String profileUrl) {
        this.id = id;
        this.artist = artist;
        this.profileUrl = profileUrl;
    }
}
