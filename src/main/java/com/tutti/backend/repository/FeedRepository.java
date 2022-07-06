package com.tutti.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.SearchArtistDtoMapping;
import com.tutti.backend.dto.Feed.SearchTitleDtoMapping;
import com.tutti.backend.dto.user.response.UserPageFeedDtoMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed,Long>, FeedQueryRepositoryCustom {

    Optional<Feed> findById(Long feedId);

    List<Feed> findAll();

    List<SearchTitleDtoMapping> findAllByTitleContainingAndPostTypeContaining(String keyword,String postType);
    List<SearchArtistDtoMapping> findAllByUser(User user);
    List<UserPageFeedDtoMapping> findTop6ByUserOrderByIdDesc(User user);
    List<UserPageFeedDtoMapping> findAllByUser_ArtistOrderByIdDesc(String user);

    List<SearchTitleDtoMapping> findAllByOrderByCreatedAtDesc();

    List<SearchTitleDtoMapping> findAllByPostTypeLikeOrderByCreatedAtDesc(String postType);

    List<SearchTitleDtoMapping> findAllByGenreAndPostTypeLike(String genre,String postType);

    List<SearchTitleDtoMapping> findAllByGenreOrderByCreatedAtDesc(String genre);

    List<SearchTitleDtoMapping> findAllByOrderByLikeCountDesc();

    List<UserPageFeedDtoMapping> findTop6ByHearts_UserAndHearts_IsHeartTrueOrderByHearts_IdDesc(User user);
    List<UserPageFeedDtoMapping> findAllByHearts_User_ArtistAndHearts_IsHeartTrueOrderByHearts_IdDesc(String Artist);


    List<SearchTitleDtoMapping> findAllByPostTypeLike(String postType);










/*    @Query(value = "SELECT * FROM  order by RAND() limit 1",nativeQuery = true)
    List<Feed> findAll();*/
}
