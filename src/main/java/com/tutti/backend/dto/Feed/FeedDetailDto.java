package com.tutti.backend.dto.Feed;

import com.tutti.backend.domain.Feed;
import lombok.Getter;

// 피드 상세 정보를 담은 middleDto
@Getter
public class FeedDetailDto {
    Long id;
    String title;
    String description;
    String albumImageUrl;
    String songUrl;
    String genre;
    String postType;
    String color;
    String artist;
    public FeedDetailDto(Feed feed, String artist) {
        this.id = feed.getId();
        this.title = feed.getTitle();
        this.description = feed.getDescription();
        this.albumImageUrl = feed.getAlbumImageUrl();
        this.songUrl = feed.getSongUrl();
        this.genre = feed.getGenre();
        this.postType = feed.getPostType();
        this.color = feed.getColor();
        this.artist = artist;
    }
}
