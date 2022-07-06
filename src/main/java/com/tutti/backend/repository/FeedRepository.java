package com.tutti.backend.repository;

import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.SearchArtistDtoMapping;
import com.tutti.backend.dto.Feed.SearchTitleDtoMapping;
import com.tutti.backend.dto.user.response.UserPageFeedDtoMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed,Long> {

    Optional<Feed> findById(Long feedId);

    List<Feed> findAll();

    List<SearchTitleDtoMapping> findAllByTitleLike(String keyword);

    List<SearchArtistDtoMapping> findAllByUser(User user);
    List<UserPageFeedDtoMapping> findTop6ByPostTypeAndUserOrderByIdDesc(String postType, User user); // 유저가 업로드[노래] 한 리스트 6개
    List<UserPageFeedDtoMapping> findAllByPostTypeAndUser_ArtistOrderByIdDesc(String postType,String user); // 유저의 업로드[노래] List를 최신순으로 전체 가져오기

    List<SearchTitleDtoMapping> findAllByOrderByCreatedAtDesc();


    List<SearchTitleDtoMapping> findAllByGenre(String genre);

    List<SearchTitleDtoMapping> findAllByGenreOrderByCreatedAtDesc(String genre);

    List<SearchTitleDtoMapping> findAllByOrderByLikeCountDesc();

    List<UserPageFeedDtoMapping> findTop6ByPostTypeAndHearts_UserAndHearts_IsHeartTrueOrderByHearts_IdDesc(String postType,User user); // 유저의 좋아요[음악] List를 최신순으로 6개만 가져오기
    List<UserPageFeedDtoMapping> findAllByPostTypeAndHearts_User_ArtistAndHearts_IsHeartTrueOrderByHearts_IdDesc(String postType,String Artist); // 유저의 좋아요[노래] List를 최신순으로 전체 가져오기












/*    @Query(value = "SELECT * FROM  order by RAND() limit 1",nativeQuery = true)
    List<Feed> findAll();*/
}
