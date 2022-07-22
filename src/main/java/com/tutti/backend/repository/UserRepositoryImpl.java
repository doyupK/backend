package com.tutti.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tutti.backend.domain.QUser;
import com.tutti.backend.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
