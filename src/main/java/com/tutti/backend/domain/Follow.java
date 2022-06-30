package com.tutti.backend.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Follow {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    // 로그인 유저
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    // 로그인 유저가 팔로우 한 Artist
    @ManyToOne(fetch = FetchType.LAZY)
    private User followingUser;

    public Follow(User user, User followingUser) {
        this.user = user;
        this.followingUser = followingUser;
    }

}
