package com.tutti.backend.controller;

import com.tutti.backend.domain.User;
import com.tutti.backend.dto.liveRoom.AddRoomRequestDto;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.LiveRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> add(@RequestPart AddRoomRequestDto addRoomRequestDto,
                                             @RequestPart MultipartFile thumbNailImage,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok().body(liveRoomService.add(addRoomRequestDto,thumbNailImage, userDetails));
    }
    @GetMapping("/chatRoom")
    public ResponseEntity<Object> liveRoomSearch () {
        return ResponseEntity.ok().body(liveRoomService.liveRoomSearch());
    }

    @GetMapping("/chatRoom/{chatRoomId}")
    public ResponseEntity<Object> liveRoomDetail (@PathVariable Long chatRoomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok().body(liveRoomService.liveRoomDetail(chatRoomId));
    }

    @DeleteMapping("/chatRoom/{chatRoomId}")
    public ResponseEntity<Object> liveRoomDelete (@PathVariable Long chatRoomId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        liveRoomService.liveRoomDelete(chatRoomId,user);
        return ResponseEntity.ok().body("chatRoom 삭제 완료");
    }



}
