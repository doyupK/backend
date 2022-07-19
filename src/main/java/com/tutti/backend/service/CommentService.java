package com.tutti.backend.service;


import com.tutti.backend.domain.Comment;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.comment.CommentRequestDto;
import com.tutti.backend.dto.comment.CommentResponseDto;
import com.tutti.backend.dto.user.ResponseDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.repository.CommentRepository;
import com.tutti.backend.repository.FeedRepository;
import com.tutti.backend.repository.UserRepository;
import com.tutti.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;

    // 코멘트 작성
    @Transactional
    public Object writeComment(Long feedId,  CommentRequestDto commentRequestDto, UserDetailsImpl userDetails) {
        CommentResponseDto commentResponseDto = new CommentResponseDto();
        // 로그인 정보 확인
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_EXISTS_USERNAME)
        );
        // 코멘트 작성 할 게시글 확인
        Feed feed = feedRepository.findById(feedId).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_FEED)
        );
        Comment comment = new Comment(user, feed, commentRequestDto);
        Comment comment1 = commentRepository.save(comment);
        commentResponseDto.setSuccess(200);
        commentResponseDto.setMessage("등록 완료!");
        commentResponseDto.setCommentId(comment1.getId());
        return commentResponseDto;
    }

    // 코멘트 수정
    @Transactional
    public Object updateComment(Long feedId,Long commentId, CommentRequestDto commentRequestDto, UserDetailsImpl userDetails) {
        ResponseDto commentResponseDto = new ResponseDto();
        // 코멘트 검색
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_COMMENT)
        );
        // 작성자 != 로그인
        if (!comment.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        // 둘 중에 어떤방식으로 해야될지??
        // comment.setComment(commentRequestDto.getComment());
        comment.update(commentRequestDto);

        commentResponseDto.setSuccess(200);
        commentResponseDto.setMessage("수정 완료!");
        return commentResponseDto;
    }

    // 코멘트 삭제
    public Object deleteComment(Long feedId, Long commentId, UserDetailsImpl userDetails) {
        ResponseDto commentResponseDto = new ResponseDto();

        // 댓글을 삭제할 꺼면 해당 유저인지 검사하자
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_COMMENT)
        );
        if (!comment.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        commentRepository.deleteById(commentId);

        commentResponseDto.setSuccess(200);
        commentResponseDto.setMessage("삭제 완료!");

        return commentResponseDto;
    }
}
