package com.tutti.backend.dto.Feed;

import com.tutti.backend.domain.User;

public interface SearchArtistDtoMapping {
    Long getId();
    String getTitle();
    String getUserArtist();
    String getGenre();
    String getAlbumImageUrl();
    String getUserProfileUrl();
}
