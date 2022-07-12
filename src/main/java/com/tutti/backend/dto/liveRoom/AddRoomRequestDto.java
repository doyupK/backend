package com.tutti.backend.dto.liveRoom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddRoomRequestDto {
    private String roomTitle;
    private String description;
    private String genre;
}
