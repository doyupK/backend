package com.tutti.backend.dto.Feed;

import com.tutti.backend.domain.Comment;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 피드 상세 정보와 코멘트를 담은 middleDto
@Setter
@Getter
public class FeedDetailResponseDto {
    FeedDetailDto feed;
    private List<FeedCommentDtoMapping> comment;


    public FeedDetailResponseDto(FeedDetailDto feedDetailDto,List<FeedCommentDtoMapping> comment){
        this.feed = feedDetailDto;
        this.comment = comment;
    }

}
