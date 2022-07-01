package com.tutti.backend.dto.comment;

import lombok.Getter;

import java.time.LocalDateTime;

// 코멘트 작성 request
@Getter
public class CommentRequestDto {
    private String comment;
    private String  modifiedAt;
}
