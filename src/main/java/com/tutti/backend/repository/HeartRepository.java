package com.tutti.backend.repository;

import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.Heart;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.MainPageFeedDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HeartRepository extends JpaRepository<Heart, Long> {
    Heart findByUser_IdAndFeed_Id(Long userId, Long feedId);

    Long countByFeedIdAndIsHeartTrue(Long id);

    List<Heart> findAllByUserAndIsHeartTrue(User user); // 해당 유저의 좋아요 리스트 다 불러오기
    List<Heart> findTop6ByUserAndIsHeartTrueOrderByIdDesc(User user);// 해당 유저의 최신 좋아요 리스트 6개만 불러오기
//    List<Heart> findAllByUser(User user);
}
