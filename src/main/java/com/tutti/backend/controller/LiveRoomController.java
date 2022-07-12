package com.tutti.backend.controller;

import com.tutti.backend.dto.liveRoom.AddRoomRequestDto;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.LiveRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class LiveRoomController {
    private final LiveRoomService liveRoomService;

    @Autowired
    public LiveRoomController(LiveRoomService liveRoomService) {
        this.liveRoomService = liveRoomService;
    }



    @PostMapping("/addLive")
    public ResponseEntity<Object> heartClick(@RequestPart AddRoomRequestDto addRoomRequestDto,
                                             @RequestPart MultipartFile thumbNailImage,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok().body(liveRoomService.add(addRoomRequestDto,thumbNailImage, userDetails));
    }

}
