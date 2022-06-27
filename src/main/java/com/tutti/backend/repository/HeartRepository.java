package com.tutti.backend.repository;

import com.tutti.backend.domain.Heart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    Heart findByUser_IdAndFeed_Id(Long userId, Long feedId);
    
    Long countByFeedIdAndIsHeartTrue(Long id);
}
