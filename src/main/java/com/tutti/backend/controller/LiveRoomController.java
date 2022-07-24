package com.tutti.backend.controller;

import com.tutti.backend.domain.User;
import com.tutti.backend.dto.liveRoom.AddRoomRequestDto;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.LiveRoomService;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class LiveRoomController {
    private final LiveRoomService liveRoomService;

    @Autowired
    public LiveRoomController(LiveRoomService liveRoomService) {
        this.liveRoomService = liveRoomService;
    }



    @PostMapping("/chatRoom")
    @Timed(value = "Create Live Room", description = "Time to create live room")
    public ResponseEntity<Object> add(@RequestPart AddRoomRequestDto addRoomRequestDto,
                                             @Nullable @RequestPart MultipartFile thumbNailImage,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok().body(liveRoomService.add(addRoomRequestDto,thumbNailImage, userDetails));
    }

    @GetMapping("/chatRoom")
    @Timed(value = "Get live room list", description = "Time to Get live room list")
    public ResponseEntity<Object> liveRoomSearch () {
        return ResponseEntity.ok().body(liveRoomService.liveRoomSearch());
    }

    @GetMapping("/chatRoom/{artist}")
    @Timed(value = "Get live room detail", description = "Time to Get live room detail")
    public ResponseEntity<Object> liveRoomDetail (@PathVariable String artist
            , @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok().body(liveRoomService.liveRoomDetail(artist));
    }

    @DeleteMapping("/chatRoom/{artist}")
    @Timed(value = "Delete live room", description = "Time to delete live room")
    public ResponseEntity<Object> liveRoomDelete (@PathVariable String artist
            , @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        liveRoomService.liveRoomDelete(artist,user);
        return ResponseEntity.ok().body("chatRoom 삭제 완료");
    }



}
