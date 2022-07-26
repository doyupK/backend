package com.tutti.backend.domain;

import com.tutti.backend.dto.comment.CommentRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String comment;

    @Column
    private String modifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedId")
    private Feed feed;

    public Comment(User user, Feed feed, CommentRequestDto commentRequestDto) {
        this.user = user;
        this.feed = feed;
        this.comment = commentRequestDto.getComment();
        this.modifiedAt = commentRequestDto.getModifiedAt();
    }

    public void update(CommentRequestDto commentRequestDto) {
        this.comment = commentRequestDto.getComment();
        this.modifiedAt = commentRequestDto.getModifiedAt();
    }
}
