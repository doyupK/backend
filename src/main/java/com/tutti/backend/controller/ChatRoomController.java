package com.tutti.backend.controller;

import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RequestMapping("/api/rooms")
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    @GetMapping("/{roomId}")
    public ResponseEntity<EnterRes> enterRoom(@PathVariable String roomId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                                              HttpResponse response) throws OpenViduJavaClientException, OpenViduHttpException, ExistSessionException, MaxPublisherException {


        return ResponseEntity.ok().body(ChatRoomService.enterRoom(roomId, userDetails, response));
    }


}

