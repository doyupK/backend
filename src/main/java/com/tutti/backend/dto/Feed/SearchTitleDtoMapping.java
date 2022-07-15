package com.tutti.backend.dto.Feed;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SearchTitleDtoMapping {
    Long getId();
    String getTitle();
    String getGenre();
    default String getArtist(){
        return getUserArtist();
    }
    String getAlbumImageUrl();
//    String getUserProfileUrl();

    @JsonIgnore
    String getUserArtist();




}
