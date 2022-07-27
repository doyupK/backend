package com.tutti.backend.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentPutResponseDto {
    int success;
    String message;
    String comment;
}