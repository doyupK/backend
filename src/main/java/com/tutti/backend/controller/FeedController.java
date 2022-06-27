package com.tutti.backend.controller;

import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.FeedRequestDto;
import com.tutti.backend.dto.Feed.FeedUpdateRequestDto;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;


    @PostMapping("/feeds/upload")
    public ResponseEntity createFeed(
            @RequestPart FeedRequestDto feedRequestDto,
            @RequestPart MultipartFile albumImage,
            @RequestPart MultipartFile song,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        feedService.createFeed(feedRequestDto,albumImage,song,user);
        return ResponseEntity.ok().body("등록 완료");
    }

    @PutMapping("/feeds/{feedId}")
    public ResponseEntity updateFeed(
            @PathVariable Long feedId,
            @RequestBody FeedUpdateRequestDto feedUpdateRequestDto
            ){
        feedService.updateFeed(feedId,feedUpdateRequestDto);
        return ResponseEntity.ok().body("수정 완료");
    }

    @GetMapping("/feeds/{feedId}")
    public ResponseEntity getFeed(
            @PathVariable Long feedId
    ){
        return ResponseEntity.ok().body(feedService.getFeed(feedId));

    }

    @DeleteMapping("/feeds/{feedId}")
    public ResponseEntity deleteFeed(
            @PathVariable Long feedId
    ){
        feedService.deleteFeed(feedId);
        return ResponseEntity.ok().body("삭제 완료");
    }


}
