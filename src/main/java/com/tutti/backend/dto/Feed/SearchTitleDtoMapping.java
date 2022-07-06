package com.tutti.backend.dto.Feed;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SearchTitleDtoMapping {
    Long getId();
    String getTitle();
    String getGenre();
    String getPostType();
    String getUserArtist();
    String getAlbumImageUrl();
//    String getUserProfileUrl();




}
