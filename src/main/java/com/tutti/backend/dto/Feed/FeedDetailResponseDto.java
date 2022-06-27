package com.tutti.backend.dto.Feed;

import com.tutti.backend.domain.Comment;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class FeedDetailResponseDto {

    private Feed feed;

    private String artist;

    private List<Comment> comment;


    public FeedDetailResponseDto(Feed feed,String artist,List<Comment> comment){
        this.feed = feed;
        this.artist = artist;
        this.comment = comment;
    }

}
