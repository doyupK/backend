package com.tutti.backend.service;


import com.tutti.backend.domain.Comment;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.CommentRequestDto;
import com.tutti.backend.dto.user.ResponseDto;
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

    public Object writeComment(Long feedId,  CommentRequestDto commentRequestDto, UserDetailsImpl userDetails) {
        ResponseDto commentResponseDto = new ResponseDto();

        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                ()-> new NullPointerException("해당 유저가 존재하지 않습니다.") // 커스텀으로 바꿀 부분
        );
        Feed feed = feedRepository.findById(feedId).orElseThrow(
                ()-> new NullPointerException("해당 피드가 존재하지 않습니다.") // 커스텀으로 바꿀 부분
        );
        Comment comment = new Comment(user, feed, commentRequestDto);
        commentRepository.save(comment);

        commentResponseDto.setSuccess(200);
        commentResponseDto.setMessage("등록 완료!");

        return commentResponseDto;
    }

    @Transactional
    public Object updateComment(Long feedId,Long commentId, CommentRequestDto commentRequestDto, UserDetailsImpl userDetails) {
        ResponseDto commentResponseDto = new ResponseDto();

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.") // 커스텀으로 바꿀 부분
        );
        if (!comment.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new IllegalArgumentException("해당 댓글의 작성자가 아닙니다."); // 커스텀으로 바꿀 부분
        }
        // 둘 중에 어떤방식으로 해야될지??
        // comment.setComment(commentRequestDto.getComment());
        comment.update(commentRequestDto);

        commentResponseDto.setSuccess(200);
        commentResponseDto.setMessage("수정 완료!");

        return commentResponseDto;
    }


    public Object deleteComment(Long feedId, Long commentId, UserDetailsImpl userDetails) {
        ResponseDto commentResponseDto = new ResponseDto();

        // 댓글을 삭제할 꺼면 해당 유저인지 검사하자
        Comment comment = commentRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.") // 커스텀으로 바꿀 부분
        );
        if (!comment.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new IllegalArgumentException("해당 댓글의 작성자가 아닙니다."); // 커스텀으로 바꿀 부분
        }
        commentRepository.deleteById(commentId);

        commentResponseDto.setSuccess(200);
        commentResponseDto.setMessage("삭제 완료!");

        return commentResponseDto;
    }
}
