package com.tutti.backend.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.QFeed;
import com.tutti.backend.domain.QHeart;
import com.tutti.backend.domain.QUser;
import com.tutti.backend.dto.Feed.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static com.tutti.backend.domain.QFeed.*;
import static com.tutti.backend.domain.QHeart.*;
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
    public List<GetFeedByPostTypeDto> getFeedByPostType(String postType, String genre) {
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
    public Slice<GetFeedByPostTypeDto> getFeedByPostTypeInfiniteScroll(String postType, String genre, Pageable pageable) {
        QueryResults<GetFeedByPostTypeDto> results =queryFactory
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
                                                .offset(pageable.getOffset())
                                                .limit(pageable.getPageSize() + 1)
                                                .fetchResults();


        List<GetFeedByPostTypeDto> content = new ArrayList<>();
        for (GetFeedByPostTypeDto getFeedByPostTypeDto: results.getResults()) {
            content.add(new GetFeedByPostTypeDto(getFeedByPostTypeDto));

        }

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public List<GetMainPageListDto> getMainPageRandomList(String audio, String recommend) {
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
                .where(feed.postType.eq(audio).and(feed.genre.eq(recommend)))
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
                    .limit(7)
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
                .limit(7)
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
                .limit(5)
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
                .limit(5)
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
    public List<GetMainPageListDto> getMainPageLoginGenreList(String audio, String genre) {
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
                .where(feed.postType.eq(audio).and(feed.genre.eq(genre)))
                .orderBy(feed.createdAt.desc())
                .fetch();
    }

    @Override
    public List<GetMainPageListDto> getMainPagLikeList() {

        NumberPath<Long> aliasQuantity = Expressions.numberPath(Long.class,"hearts");

    return null;
         /*queryFactory
                .select(new QGetMainPageLikesListDto(
                        feed.id,
                        feed.title,
                        user.artist,
                        feed.genre,
                        feed.albumImageUrl.as("albumImageUrl"),
                        feed.hearts.coun
                ))
                .from(feed)
                .leftJoin(feed.user,user)
                .where(feed.postType.eq("audio"))
                .orderBy()
                .fetch();*/
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
