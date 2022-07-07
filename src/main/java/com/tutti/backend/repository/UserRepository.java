package com.tutti.backend.repository;


import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.SearchArtistDtoMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>,UserRepositoryCustom {
    Optional<User> findByEmail(String signupRequestDto);

    Optional<User> findByKakaoId(Long id);


    Optional<User> findByArtist(String artist);

    User findByArtistLike(String keyword);


}
