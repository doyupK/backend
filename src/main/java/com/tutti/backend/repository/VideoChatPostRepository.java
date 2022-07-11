package com.tutti.backend.repository;

import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.VideoChatPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoChatPostRepository extends JpaRepository<VideoChatPost,Long> {
}
