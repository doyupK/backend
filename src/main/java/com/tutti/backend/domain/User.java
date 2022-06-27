package com.tutti.backend.domain;

import com.tutti.backend.dto.user.FileRequestDto;
import com.tutti.backend.dto.user.SignupRequestDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity(name = "user_table")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String artist;

    @Column
    private String profileUrl;

    @Column(unique = true)
    private Long kakaoId;

    @Column
    private String googleId;

    @Column
    private String profileText;

    @Column
    private String instagramUrl;

    @Column
    private String youtubeUrl;

    @Column
    private String favoriteGenre1;

    @Column
    private String favoriteGenre2;

    @Column
    private String favoriteGenre3;

    @Column
    private String favoriteGenre4;

    @CreationTimestamp // INSERT 시 자동으로 값을 채워줌
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp // UPDATE 시 자동으로 값을 채워줌
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserConfirmEnum userConfirmEnum;


    public User(SignupRequestDto signupRequestDto, String password, FileRequestDto fileRequestDto) {
        this.email = signupRequestDto.getEmail();
        this.artist = signupRequestDto.getArtist();
        this.profileUrl = fileRequestDto.getImageUrl();
        this.profileText = signupRequestDto.getProfileText();
        this.instagramUrl = signupRequestDto.getInstagramUrl();
        this.youtubeUrl = signupRequestDto.getYoutubeUrl();
        this.favoriteGenre1 = signupRequestDto.getGenre()[0];
        this.favoriteGenre2 = signupRequestDto.getGenre()[1];
        this.favoriteGenre3 = signupRequestDto.getGenre()[2];
        this.favoriteGenre4 = signupRequestDto.getGenre()[3];
        this.password = password;
        this.kakaoId = null;
        this.userConfirmEnum = UserConfirmEnum.BEFORE_CONFIRM;
    }
}
