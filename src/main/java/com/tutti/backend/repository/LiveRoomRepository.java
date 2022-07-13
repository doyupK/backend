package com.tutti.backend.repository;

import com.tutti.backend.domain.LiveRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveRoomRepository extends JpaRepository<LiveRoom, Long>,LiveRoomRepositoryCustom {



}
