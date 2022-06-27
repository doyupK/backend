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
public class Feed extends Timestamped{

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
    private String postType;

    @Column(nullable = false)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private User user;

    public Feed (String title,
                 String description,
                 String albumImageUrl,
                 String songUrl,
                 String genre,
                 String postType,
                 String color,
                 User user){

        this.title = title;
        this.description = description;
        this.albumImageUrl = albumImageUrl;
        this.songUrl = songUrl;
        this.genre = genre;
        this.postType = postType;
        this.color = color;
        this.user=user;
    }

    public void update(FeedUpdateRequestDto feedUpdateRequestDto){
        this.title = feedUpdateRequestDto.getTitle();
        this.description = feedUpdateRequestDto.getDescription();
        this.color = feedUpdateRequestDto.getColor();
    }


}
