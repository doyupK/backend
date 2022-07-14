package com.tutti.backend.dto.liveRoom;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class LiveRoomSearchDto {
    int success;
    String message;
    List<LiveRoomListDto> results;
}
