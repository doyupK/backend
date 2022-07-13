package com.tutti.backend.repository;

import com.tutti.backend.domain.LiveRoom;
import com.tutti.backend.dto.liveRoom.LiveRoomListDto;

import java.util.List;

public interface LiveRoomRepositoryCustom {


    List<LiveRoomListDto> searchAllLiveRooms();

    LiveRoomListDto searchLiveRoom(Long id);



}
