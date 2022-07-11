package com.tutti.backend.repository;

import com.tutti.backend.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel,Long> {
}
