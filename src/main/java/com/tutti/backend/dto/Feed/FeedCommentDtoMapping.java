package com.tutti.backend.dto.Feed;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface FeedCommentDtoMapping {
    Long getId();
    String getComment();
    String getUserArtist();


}
