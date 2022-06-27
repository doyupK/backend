package com.tutti.backend.controller;

import com.tutti.backend.dto.CommentRequestDto;
import com.tutti.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    //required = false 쓸까 말까? // commentId 필요한가?
    // 댓글 작성
    @PostMapping("/feeds/{feedId}")
    public ResponseEntity<Object> writeComment (@PathVariable(name = "feedId") Long feedId,
                                                @RequestBody CommentRequestDto commentRequestDto,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(commentService.writeComment(feedId,commentRequestDto ,userDetails));
    }
    // feedId는 url로 받고 coommentid는 requestDto에 담아서 온다?
    //
    // 댓글 수정
    @PutMapping("/feeds/{feedId}/{commentId}")
    public ResponseEntity<Object> updateComment (@PathVariable(name = "feedId") Long feedId,
                                                 @PathVariable(name = "commentId") Long commentId,
                                                 @RequestBody CommentRequestDto commentRequestDto,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(commentService.updateComment(feedId,commentId,commentRequestDto,userDetails));
    }

    // 댓글 삭제
    @DeleteMapping("/feeds/{feedId}/{commentId}")
    public ResponseEntity<Object> deleteComment (@PathVariable(name = "feedId") Long feedId,
                                                 @PathVariable(name = "commentId") Long commentId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(commentService.deleteComment(feedId, commentId, userDetails));
    }
}
