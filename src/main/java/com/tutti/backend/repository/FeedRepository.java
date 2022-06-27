package com.tutti.backend.repository;

import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.SearchArtistDtoMapping;
import com.tutti.backend.dto.Feed.SearchTitleDtoMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed,Long> {

    Optional<Feed> findById(Long feedId);

    List<Feed> findAll();

    List<SearchTitleDtoMapping> findAllByTitleLike(String keyword);

    List<SearchArtistDtoMapping> findAllByUser(User user);

    List<SearchTitleDtoMapping> findAllByOrderByCreatedAtDesc();


    List<SearchTitleDtoMapping> findAllByGenre(String genre);

    List<SearchTitleDtoMapping> findAllByGenreOrderByCreatedAtDesc(String genre);












/*    @Query(value = "SELECT * FROM  order by RAND() limit 1",nativeQuery = true)
    List<Feed> findAll();*/
}
