package com.tutti.backend.repository;

import com.tutti.backend.domain.LiveRoom;
import com.tutti.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveRoomRepository extends JpaRepository<LiveRoom, Long>,LiveRoomRepositoryCustom {

    List<LiveRoom> findAllByUserAndOnAirTrue(User user);

    LiveRoom findByUser(User user);



}
