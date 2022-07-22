package com.tutti.backend.repository;

import com.tutti.backend.domain.User;

public interface UserRepositoryCustom {

    User getUserByKeyword(String keyword);

}
