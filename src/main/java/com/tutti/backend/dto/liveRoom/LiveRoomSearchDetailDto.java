package com.tutti.backend.dto.liveRoom;

import com.tutti.backend.domain.LiveRoom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiveRoomSearchDetailDto {

    int success;
    String message;

    LiveRoomListDto liveRoomListDto;
}
