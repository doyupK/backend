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
                        user.artist,
                        feed.genre,
                        feed.albumImageUrl.as("albumImageUrl")
                ))
                .from(feed)
                .join(feed.user,user)
                .where(feed.postType.eq(audio))
                .fetch();
    }

    @Override
    public List<GetMainPageListDto> searchMusicByTitleKeyword(String keyword) {
        return queryFactory
                    .select(new QGetMainPageListDto(
                                feed.id,
                                feed.title,
                                user.artist,
                                feed.genre,
                                feed.albumImageUrl.as("albumImageUrl")
                        ))
                    .from(feed)
                    .join(feed.user,user)
                    .where(feed.postType.eq("audio").and(feed.title.contains(keyword)))
                    .limit(6)
                    .fetch();
    }



    @Override
    public List<GetMainPageListDto> searchMusicByArtistKeyword(String keyword) {
        return queryFactory
                .select(new QGetMainPageListDto(
                        feed.id,
                        feed.title,
                        user.artist,
                        feed.genre,
                        feed.albumImageUrl.as("albumImageUrl")
                ))
                .from(feed)
                .join(feed.user,user)
                .where(feed.postType.eq("audio").and(feed.user.artist.contains(keyword)))
                .limit(6)
                .fetch();
    }

    @Override
    public List<GetMainPageListDto> searchVideoByTitleKeyword(String keyword) {
        return queryFactory
                .select(new QGetMainPageListDto(
                        feed.id,
                        feed.title,
                        user.artist,
                        feed.genre,
                        feed.albumImageUrl.as("albumImageUrl")
                ))
                .from(feed)
                .join(feed.user,user)
                .where(feed.postType.eq("video").and(feed.title.contains(keyword)))
                .limit(4)
                .fetch();
    }

    @Override
    public List<GetMainPageListDto> searchVideoByArtistKeyword(String keyword) {
        return queryFactory
                .select(new QGetMainPageListDto(
                        feed.id,
                        feed.title,
                        user.artist,
                        feed.genre,
                        feed.albumImageUrl.as("albumImageUrl")
                ))
                .from(feed)
                .join(feed.user,user)
                .where(feed.postType.eq("video").and(feed.user.artist.contains(keyword)))
                .limit(4)
                .fetch();
    }

    @Override
    public List<GetMainPageListDto> searchCategoryByKeyword(String category, String keyword) {
        return queryFactory
                .select(new QGetMainPageListDto(
                        feed.id,
                        feed.title,
                        user.artist,
                        feed.genre,
                        feed.albumImageUrl.as("albumImageUrl")
                ))
                .from(feed)
                .join(feed.user,user)
                .where(categoryEq(category,keyword))
                .fetch();
    }

    @Override
    public List<GetMainPageListDto> getMainPageLatestList(String audio) {
        return queryFactory
                .select(new QGetMainPageListDto(
                        feed.id,
                        feed.title,
                        user.artist,
                        feed.genre,
                        feed.albumImageUrl.as("albumImageUrl")
                ))
                .from(feed)
                .join(feed.user,user)
                .where(feed.postType.eq(audio))
                .orderBy(feed.createdAt.desc())
                .fetch();
    }

    @Override
    public List<GetMainPageListDto> getMainPageVideoList(String video) {
        return queryFactory
                .select(new QGetMainPageListDto(
                        feed.id,
                        feed.title,
                        user.artist,
                        feed.genre,
                        feed.albumImageUrl.as("albumImageUrl")
                ))
                .from(feed)
                .join(feed.user,user)
                .where(feed.postType.eq(video))
                .orderBy(feed.createdAt.desc())
                .fetch();
    }

    @Override
    public List<GetMainPageListDto> getMainPageLoginGenreList(String genre) {
        return queryFactory
                .select(new QGetMainPageListDto(
                        feed.id,
                        feed.title,
                        user.artist,
                        feed.genre,
                        feed.albumImageUrl.as("albumImageUrl")
                ))
                .from(feed)
                .join(feed.user,user)
                .where(feed.postType.eq("music").and(feed.genre.eq(genre)))
                .orderBy(feed.createdAt.desc())
                .fetch();
    }

    private BooleanExpression categoryEq(String category,String keyword) {
        switch (category) {
            case "musicTitle":
                return feed.postType.eq("audio").and(feed.title.contains(keyword));
            case "musicArtist":
                return feed.postType.eq("audio").and(feed.user.artist.contains(keyword));
            case "videoTitle":
                return feed.postType.eq("video").and(feed.title.contains(keyword));
            case "videoArtist":
                return feed.postType.eq("video").and(feed.user.artist.contains(keyword));
        }
        return null;
    }

    private BooleanExpression postTypeEq(String postType) {
        return hasText(postType)?feed.postType.eq(postType):null;
    }

    private BooleanExpression genreEq(String genre) {
        return hasText(genre)?feed.genre.eq(genre):null;
    }

}
