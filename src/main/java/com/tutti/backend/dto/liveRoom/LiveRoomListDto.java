package com.tutti.backend.dto.liveRoom;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class LiveRoomListDto {

    Long id;

    String roomTitle;

    String description;

    String artist;

    String profileImageUrl;

    String thumbnailImageUrl;



    @QueryProjection
    public LiveRoomListDto(Long id, String roomTitle, String description, String artist, String profileImageUrl, String thumbnailImageUrl) {
        this.id = id;
        this.roomTitle = roomTitle;
        this.description = description;
        this.artist = artist;
        this.profileImageUrl = profileImageUrl;
        this.thumbnailImageUrl = thumbnailImageUrl;
    }
}
