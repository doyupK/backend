package com.tutti.backend.controller;

import com.tutti.backend.domain.User;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.VideoChatPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class VideoChatPostController {

    private VideoChatPostService videoChatPostService;


    @PostMapping("/videoChatPost")
    public ResponseEntity<?> createPost(
            @RequestPart PostRequestDto requestDto,
            @RequestPart MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        User user = userDetails.getUser();

        return ResponseEntity.ok().body("화상채팅 등록 완료");
    }

}
