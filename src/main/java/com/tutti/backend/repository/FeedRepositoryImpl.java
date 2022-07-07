package com.tutti.backend.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.QFeed;
import com.tutti.backend.domain.QUser;
import com.tutti.backend.dto.Feed.GetFeedByPostTypeDto;
import com.tutti.backend.dto.Feed.GetMainPageListDto;
import com.tutti.backend.dto.Feed.QGetFeedByPostTypeDto;
import com.tutti.backend.dto.Feed.QGetMainPageListDto;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.tutti.backend.domain.QFeed.*;
import static com.tutti.backend.domain.QUser.user;
import static org.springframework.util.StringUtils.hasText;

public class FeedRepositoryImpl implements FeedRepositoryCustom{


    @PersistenceContext
    EntityManager em;

    private final JPAQueryFactory queryFactory;

    public FeedRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<GetFeedByPostTypeDto> getFeedByPostType(String postType,String genre) {
        return queryFactory
                .select(new QGetFeedByPostTypeDto(
                        feed.id,
                        feed.title,
                        user.artist,
                        feed.genre,
                        feed.albumImageUrl.as("albumImageUrl"),
                        user.profileUrl.as("userProfileImageUrl")
                ))
                .from(feed)
                .join(feed.user,user)
                .where(postTypeEq(postType),
                        genreEq(genre))
                .orderBy(feed.createdAt.desc())
                .fetch();
    }

    @Override
    public List<GetMainPageListDto> getMainPageRandomList(String audio) {
        return queryFactory
                .select(new QGetMainPageListDto(
                        feed.id,
                        feed.title,
                        feed.genre,
                        user.artist,
                        feed.albumImageUrl.as("albumImageUrl")
                ))
                .from(feed)
                .join(feed.user,user)
                .where(feed.postType.eq(audio))
                .fetch();
    }

    private BooleanExpression postTypeEq(String postType) {
        return hasText(postType)?feed.postType.eq(postType):null;
    }

    private BooleanExpression genreEq(String genre) {
        return hasText(genre)?feed.genre.eq(genre):null;
    }

}
