package com.tutti.backend.controller;

import com.tutti.backend.dto.comment.CommentRequestDto;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.CommentService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;


    // 댓글 작성
    @PostMapping("/feeds/{feedId}")
    @Timed(value = "Write Comment", description = "Time to Comment write")
    public ResponseEntity<Object> writeComment (@PathVariable(name = "feedId") Long feedId,
                                                @RequestBody @Valid CommentRequestDto commentRequestDto,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(commentService.writeComment(feedId,commentRequestDto ,userDetails));
    }

    // 댓글 수정
    @PutMapping("/feeds/{feedId}/{commentId}")
    @Timed(value = "Modify Comment", description = "Time to Comment Modify")
    public ResponseEntity<Object> updateComment (@PathVariable(name = "feedId") Long feedId,
                                                 @PathVariable(name = "commentId") Long commentId,
                                                 @RequestBody CommentRequestDto commentRequestDto,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(commentService.updateComment(feedId,commentId,commentRequestDto,userDetails));
    }

    // 댓글 삭제
    @DeleteMapping("/feeds/{feedId}/{commentId}")
    @Timed(value = "Delete Comment", description = "Time to Comment Delete")
    public ResponseEntity<Object> deleteComment (@PathVariable(name = "feedId") Long feedId,
                                                 @PathVariable(name = "commentId") Long commentId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(commentService.deleteComment(feedId, commentId, userDetails));
    }
}