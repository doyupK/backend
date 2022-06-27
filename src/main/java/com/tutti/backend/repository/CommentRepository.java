package com.tutti.backend.repository;

import com.tutti.backend.domain.Comment;
import com.tutti.backend.domain.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository <Comment, Long> {

//    void deleteByIdAndUser_UserId(Long commentId, Long userId);

    List<Comment> findAllByFeed(Feed feed);


}
