package com.tutti.backend.dto.Feed;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class GetUserPageListDto {
    private Long feedId;

    private String title;

    private String artist;

    private String genre;

    private String albumImageUrl;

    private String profileUrl;


    @QueryProjection
    public GetUserPageListDto(Long feedId, String title, String artist, String genre, String albumImageUrl,String profileUrl) {
        this.feedId = feedId;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.albumImageUrl = albumImageUrl;
        this.profileUrl = profileUrl;
    }
}
