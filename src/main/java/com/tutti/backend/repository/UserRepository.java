package com.tutti.backend.repository;


import com.tutti.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String signupRequestDto);

    Optional<User> findByKakaoId(Long id);


    Optional<User> findByArtist(String artist);
}
