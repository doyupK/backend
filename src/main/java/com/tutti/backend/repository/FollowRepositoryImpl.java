package com.tutti.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tutti.backend.domain.QUser;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.user.GetFollowingDto;
import com.tutti.backend.dto.user.QGetFollowingDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.tutti.backend.domain.QFollow.follow;

public class FollowRepositoryImpl implements FollowRepositoryCustom {

    @PersistenceContext
    EntityManager em;

    private final JPAQueryFactory queryFactory;

    public FollowRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<GetFollowingDto> getTop7ByUserOrderById(User currentUser) {
        QUser nowUser = new QUser("nowUser");
        QUser followUser = new QUser("followingUser");
        return queryFactory
                .select(new QGetFollowingDto(
                        followUser.profileUrl,
                        followUser.id,
                        followUser.artist))
                .from(follow)
                .join(follow.user,nowUser)
                .join(follow.followingUser,followUser)
                .where(nowUser.artist.eq(currentUser.getArtist()))
                .limit(7)
                .orderBy(follow.id.desc())
                .fetch();
    }
}
