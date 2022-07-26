package com.tutti.backend.dto.comment;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

// 코멘트 작성 request
@Getter
public class CommentRequestDto {
    @NotBlank(message = "내용을 입력해주세요.")
    private String comment;
    private String  modifiedAt;
}
