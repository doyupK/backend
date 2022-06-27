package com.tutti.backend.repository;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tutti.backend.domain.Feed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FeedQueryRepositoryImpl implements FeedQueryRepository {

    private final JPAQueryFactory query;



    @Override
    public List<Feed> getRandomList() {
       /* return query
                .select(new List<Feed>)
                .from(Feed)
                .orderBy(NumberExpression.random().asc())*/
        return null;
    }
}
