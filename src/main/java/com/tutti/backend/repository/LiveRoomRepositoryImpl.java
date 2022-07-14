package com.tutti.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tutti.backend.domain.LiveRoom;
import com.tutti.backend.domain.QLiveRoom;
import com.tutti.backend.domain.QUser;
import com.tutti.backend.dto.liveRoom.LiveRoomListDto;
import com.tutti.backend.dto.liveRoom.QLiveRoomListDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.tutti.backend.domain.QLiveRoom.liveRoom;
import static com.tutti.backend.domain.QUser.user;

public class LiveRoomRepositoryImpl implements LiveRoomRepositoryCustom {

    @PersistenceContext
    EntityManager em;

    private final JPAQueryFactory queryFactory;

    public LiveRoomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<LiveRoomListDto> searchAllLiveRooms() {
        return queryFactory.select(new QLiveRoomListDto(
                                                liveRoom.id,
                                                liveRoom.roomTitle,
                                                liveRoom.description,
                                                user.artist,
                                                user.profileUrl,
                                                liveRoom.thumbnailImageUrl
                                        ))
                                                .from(liveRoom)
                                                .join(liveRoom.user,user)
                                                .where(liveRoom.onAir.eq(true))
                                                .orderBy(liveRoom.createdAt.desc())
                                                .fetch();
                                    }

    @Override
    public LiveRoomListDto searchLiveRoom(String artist) {
        return queryFactory.select(new QLiveRoomListDto(
                                            liveRoom.id,
                                            liveRoom.roomTitle,
                                            liveRoom.description,
                                            user.artist,
                                            user.profileUrl,
                                            liveRoom.thumbnailImageUrl
                                    ))
                                .from(liveRoom)
                                .join(liveRoom.user,user)
                                .where(liveRoom.user.artist.eq(artist).and(liveRoom.onAir.eq(true)))
                                .fetchOne();
    }
}
