package com.tutti.backend.repository;

import com.tutti.backend.domain.User;
import com.tutti.backend.dto.user.GetFollowingDto;

import java.util.List;

public interface FollowRepositoryCustom {
    List<GetFollowingDto> getTop7ByUserOrderById(User user);
}
