package com.tutti.backend.dto.Feed;

import lombok.Data;

import java.util.List;

@Data
public class SearchArtistAllByKeywordDto {
    int success;
    String message;
    List<GetArtistListDto> results;
}
