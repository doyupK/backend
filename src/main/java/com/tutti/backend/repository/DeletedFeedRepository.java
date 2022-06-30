package com.tutti.backend.repository;

import com.tutti.backend.domain.DeletedFeed;
import com.tutti.backend.domain.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeletedFeedRepository  extends JpaRepository<DeletedFeed,Long> {
}
