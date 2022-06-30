package com.tutti.backend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Heart {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedId")
    private Feed feed;

    @Column
    private Boolean isHeart;


    public Heart(User user, Feed feed) {
        this.user = user;
        this.feed = feed;
        this.isHeart = true;
    }

    public void update() {
        this.isHeart = !this.isHeart;
    }
}
