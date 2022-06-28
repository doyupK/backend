package com.tutti.backend.repository;

import com.tutti.backend.domain.Comment;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.dto.Feed.FeedCommentDtoMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository <Comment, Long> {

//    void deleteByIdAndUser_UserId(Long commentId, Long userId);

    List<FeedCommentDtoMapping> findAllByFeed(Feed feed);


}
