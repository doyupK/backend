package com.tutti.backend.repository;

import com.tutti.backend.domain.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed,Long> {

    Optional<Feed> findById(Long feedId);
}
