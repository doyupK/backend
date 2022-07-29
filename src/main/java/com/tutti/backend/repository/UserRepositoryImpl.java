package com.tutti.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tutti.backend.domain.QUser;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.GetArtistListDto;
import com.tutti.backend.dto.Feed.QGetArtistListDto;
import com.tutti.backend.dto.Feed.QGetMainPageListDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static com.tutti.backend.domain.QFeed.feed;
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

    @Override
    public List<GetArtistListDto> searchArtistByArtistKeyword(String keyword) {
        return queryFactory
                .select(new QGetArtistListDto(
                        user.id,
                        user.artist,
                        user.profileUrl
                ))
                .from(user)
                .where(user.artist.contains(keyword))
                .limit(7)
                .fetch();
    }

    @Override
    public List<GetArtistListDto> searchArtistAllByArtistKeyword(String keyword) {
        return queryFactory
                .select(new QGetArtistListDto(
                        user.id,
                        user.artist,
                        user.profileUrl
                ))
                .from(user)
                .where(user.artist.contains(keyword))
                .fetch();
    }
}
