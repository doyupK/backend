package com.tutti.backend.dto.Feed;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class GetMainPageLikesListDto {
    private Long id;

    private String title;

    private String artist;

    private String genre;

    private String albumImageUrl;

    private Long likes;


    @QueryProjection
    public GetMainPageLikesListDto(Long id, String title, String artist, String genre, String albumImageUrl,Long likes) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.albumImageUrl = albumImageUrl;
        this.likes = likes;
    }
}
