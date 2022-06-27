package com.tutti.backend.repository;

import com.tutti.backend.domain.Follow;
import com.tutti.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository <Follow, Long> {


}
