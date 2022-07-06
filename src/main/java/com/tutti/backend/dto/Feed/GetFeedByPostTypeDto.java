package com.tutti.backend.dto.Feed;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class GetFeedByPostTypeDto {

    private Long id;

    private String title;

    private String artist;

    private String genre;

    private String albumImageUrl;

    private String profileImageUrl;

    @QueryProjection
    public GetFeedByPostTypeDto(Long id, String title,String artist, String genre, String albumImageUrl, String profileImageUrl) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.albumImageUrl = albumImageUrl;
        this.profileImageUrl = profileImageUrl;
    }
}
