package com.tutti.backend.domain;

import com.tutti.backend.dto.Feed.FeedUpdateRequestDto;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class DeletedFeed extends Timestamped {

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id
        private Long id;


        @Column(nullable = false)
        private String title;

        @Column(nullable = false)
        private String description;


        @Column(nullable = false)
        private String albumImageUrl;

        @Column(nullable = false)
        private String songUrl;

        @Column(nullable = false)
        private String genre;

        @Column(nullable = false)
        private String postType; // 동영상 or 음악파일

        @Column(nullable = false)
        private String color;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "user_id")
        private User user;




        public DeletedFeed (Feed feed){

            this.title = feed.getTitle();
            this.description = feed.getDescription();
            this.albumImageUrl = feed.getAlbumImageUrl();
            this.songUrl = feed.getSongUrl();
            this.genre = feed.getGenre();
            this.postType = feed.getPostType();
            this.color = feed.getColor();
            this.user=feed.getUser();
        }

        public void update(FeedUpdateRequestDto feedUpdateRequestDto){
            this.title = feedUpdateRequestDto.getTitle();
            this.description = feedUpdateRequestDto.getDescription();
            this.color = feedUpdateRequestDto.getColor();
        }

    }
