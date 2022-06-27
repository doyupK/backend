package com.tutti.backend.service;


import com.tutti.backend.domain.Comment;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.CommentRequestDto;
import com.tutti.backend.dto.user.ResponseDto;
import com.tutti.backend.repository.CommentRepository;
import com.tutti.backend.repository.FeedRepository;
import com.tutti.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;

    public Object writeComment(Long feedId,  CommentRequestDto commentRequestDto, UserDetails userDetails) {
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                ()-> new NullPointerException("해당 유저가 존재하지 않습니다.") // 커스텀으로 바꿀 부분
        );
        Feed feed = feedRepository.findById(feedId).orElseThrow(
                ()-> new NullPointerException("해당 피드가 존재하지 않습니다.") // 커스텀으로 바꿀 부분
        );
        Comment comment = new Comment(user, feed, commentRequestDto);
        commentRepository.save(comment);
        return new ResponseDto(200,"등록 완료!");
    }

    @Transactional
    public Object updateComment(Long feedId,Long commentId, CommentRequestDto commentRequestDto, UserDetails userDetails) {
        Comment comment = commentRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.") // 커스텀으로 바꿀 부분
        );
        if (!comment.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new IllegalArgumentException("해당 댓글의 작성자가 아닙니다."); // 커스텀으로 바꿀 부분
        }
        // 둘 중에 어떤방식으로 해야될지??
        // comment.setComment(commentRequestDto.getComment());
        // comment.update(commentRequestDto);


        return new ResponseDto(200,"수정 완료!");
    }


    public Object deleteComment(Long feedId, Long commentId, UserDetails userDetails) {
        // 해당 댓글id + 유저id를 가진 댓글을 삭제 이렇게 or 밑에처럼
//        CommentRepository.deleteByIdAndUser_UserId(commentId,userDetails.getUser().getId()).orElseThrow(
//                () -> new NullPointerException("?? 댓글을 삭제할 수 없다?")
//        );
        // 댓글을 삭제할 꺼면 해당 유저인지 검사하자
        Comment comment = commentRepository.findById(feedId).orElseThrow(
                () -> new NullPointerException("해당 댓글이 존재하지 않습니다.") // 커스텀으로 바꿀 부분
        );
        if (!comment.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new IllegalArgumentException("해당 댓글의 작성자가 아닙니다."); // 커스텀으로 바꿀 부분
        }
        commentRepository.deleteById(commentId);

        return new ResponseDto(200,"삭제 완료!");
    }
}
