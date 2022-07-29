package com.tutti.backend.repository;

import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.GetArtistListDto;

import java.util.List;

public interface UserRepositoryCustom {

    User getUserByKeyword(String keyword);

    List<GetArtistListDto> searchArtistByArtistKeyword(String keyword);

    List<GetArtistListDto> searchArtistAllByArtistKeyword(String keyword);

}
