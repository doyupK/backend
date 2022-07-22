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
    @QueryProjection
    public GetFeedByPostTypeDto(GetFeedByPostTypeDto getFeedByPostTypeDto) {
        this.id = getFeedByPostTypeDto.getId();
        this.title = getFeedByPostTypeDto.getTitle();
        this.artist = getFeedByPostTypeDto.getArtist();
        this.genre = getFeedByPostTypeDto.getGenre();
        this.albumImageUrl = getFeedByPostTypeDto.getAlbumImageUrl();
        this.profileImageUrl = getFeedByPostTypeDto.getProfileImageUrl();
    }
}
