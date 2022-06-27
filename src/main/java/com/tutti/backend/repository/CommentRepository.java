package com.tutti.backend.repository;

import com.tutti.backend.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository <Comment, Long> {

//    void deleteByIdAndUser_UserId(Long commentId, Long userId);
}
