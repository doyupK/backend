package com.tutti.backend.controller;

import com.tutti.backend.chat.model.ChatMessage;
import com.tutti.backend.chat.service.ChatService;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.PostRequestDto;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class ChannelController {

    private ChannelService channelService;

    private ChatService chatService;


    @PostMapping("/channel")
    public ResponseEntity<?> createPost(
            @RequestPart PostRequestDto requestDto,
            @RequestPart MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        User user = userDetails.getUser();
        channelService.createPost(user,requestDto,file);

        return ResponseEntity.ok().body("화상채팅 등록 완료");
    }
    @GetMapping("/channel")
    public ResponseEntity<?> readPost(
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        User user = userDetails.getUser();

        return ResponseEntity.ok().body(channelService.readPost(user));
    }


    @GetMapping("/channel/{channelId}")
    public ResponseEntity<?> readPostDetail(
            @PathVariable Long channelId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        User user = userDetails.getUser();
        chatService.save(message, token);
        return ResponseEntity.ok().body(channelService.readPostDetail(user,channelId));
    }

    

}
