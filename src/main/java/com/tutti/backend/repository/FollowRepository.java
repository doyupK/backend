package com.tutti.backend.repository;

import com.tutti.backend.domain.Follow;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.user.FollowingDtoMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository <Follow, Long> {
    Long countByUser(User user);
    Long countByFollowingUser(User user);
    Follow findByUserAndFollowingUser_Artist(User user, String otherArtist);
    Boolean existsByUserAndFollowingUser_Artist(User user, String otherArtist);
    List<FollowingDtoMapping> findByUser(User user); // 팔로잉 전체 불러오기
    List<FollowingDtoMapping> findTop7ByUserOrderById(User user); // 유저가 팔로우 한 리스트 7개
    List<FollowingDtoMapping> findAllByUser_Artist(String user); // 유저의 팔로잉 List를 최신순으로 전체 가져오기
}
