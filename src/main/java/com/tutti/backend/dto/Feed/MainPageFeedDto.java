package com.tutti.backend.dto.Feed;


import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 메인 페이지 피드 정보 담은 middle Dto
@Getter
@NoArgsConstructor
public class MainPageFeedDto {

    private Long feedId;

    private String title;

    private String artist;

    private String genre;

    private String albumImage;

    private String profileImage;


    public MainPageFeedDto(Feed feed, User user){
        this.feedId = feed.getId();
        this.title = feed.getTitle();
        this.artist = user.getArtist();
        this.genre = feed.getGenre();
        this.albumImage = feed.getAlbumImageUrl();
        this.profileImage = user.getProfileUrl();
    }


}
