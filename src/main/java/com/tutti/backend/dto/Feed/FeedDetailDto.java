package com.tutti.backend.dto.Feed;

import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.Heart;
import lombok.Getter;

// 피드 상세 정보를 담은 middleDto
@Getter
public class FeedDetailDto {
    Long id;
    String title;
    String musicTitle;
    String description;
    String albumImageUrl;
    String songUrl;
    String genre;
    String postType;
    String color;
    String artist;
    String profileUrl;
    int likeCount;
    boolean flag = false;

    public FeedDetailDto(Feed feed, String artist, String profileUrl, boolean heartCheck) {
        this.id = feed.getId();
        this.title = feed.getTitle();
        this.musicTitle = feed.getMusicTitle();
        this.description = feed.getDescription();
        this.albumImageUrl = feed.getAlbumImageUrl();
        this.songUrl = feed.getSongUrl();
        this.genre = feed.getGenre();
        this.postType = feed.getPostType();
        this.color = feed.getColor();
        this.artist = artist;
        this.profileUrl = profileUrl;
        this.likeCount = feed.getLikeCount();
        this.flag = heartCheck;

    }
}
