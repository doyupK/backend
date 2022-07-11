package com.tutti.backend.domain;

import com.tutti.backend.dto.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Channel
{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false)
    private String thumbNailImageUrl;


    public Channel(PostRequestDto requestDto, User user, String thumbNailImageUrl ){
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.user = user;
        this.artist = user.getArtist();
        this.profileImageUrl =user.getProfileUrl();
        this.thumbNailImageUrl = thumbNailImageUrl;
    }
}
