package com.tutti.backend.repository;

import com.tutti.backend.domain.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed,Long> {

    Optional<Feed> findById(Long feedId);

    List<Feed> findAll();

    @Query(value = "SELECT * FROM  order by RAND() limit 1",nativeQuery = true)
    List<Feed> findAll();
}
