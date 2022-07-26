package com.tutti.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import static com.tutti.backend.domain.QUser.user;

import static com.tutti.backend.domain.QFollow.follow;

import com.tutti.backend.domain.QFollow;
import com.tutti.backend.domain.QUser;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.GetUserPageListDto;
import com.tutti.backend.dto.user.GetFollowingDto;
import com.tutti.backend.dto.user.QGetFollowingDto;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static com.tutti.backend.domain.QUser.user;

public class UserRepositoryImpl implements UserRepositoryCustom{

    @PersistenceContext
    EntityManager em;

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public User getUserByKeyword(String keyword) {
        return null;
        /* queryFactory
                .select(user.id)
                .from(user)
                .where();*/
    }


}
