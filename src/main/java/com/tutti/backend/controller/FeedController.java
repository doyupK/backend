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



    @GetMapping("/")
    public ResponseEntity<?> getMainPage(@AuthenticationPrincipal UserDetailsImpl userDetails){
        if(userDetails.getUser()!=null){
            return ResponseEntity.ok().body(feedService.getMainPageByUser(userDetails.getUser()));
        }

        return feedService.getMainPage();
    }

    @GetMapping("/feeds")
    public ResponseEntity<?> getFeedPage(){
        return feedService.getFeedPage();
    }
    @GetMapping("/feeds/search")
    public ResponseEntity<?> getFeedByGenrePage(@RequestParam String genre){
        return feedService.getFeedByGenrePage(genre);
    }




    @PostMapping("/feeds/upload")
    public ResponseEntity<?> createFeed(
            @RequestPart FeedRequestDto feedRequestDto,
            @RequestPart MultipartFile albumImage,
            @RequestPart MultipartFile song,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser();
        feedService.createFeed(feedRequestDto,albumImage,song,user);
        return ResponseEntity.ok().body("피드 등록 완료");
    }

    @PutMapping("/feeds/{feedId}")
    public ResponseEntity<?> updateFeed(
            @PathVariable Long feedId,
            @RequestBody FeedUpdateRequestDto feedUpdateRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
            ){
        User user = userDetails.getUser();
        feedService.updateFeed(feedId,feedUpdateRequestDto,user);
        return ResponseEntity.ok().body("피드 수정 완료");
    }

    @GetMapping("/feeds/{feedId}")
    public ResponseEntity<?> getFeed(
            @PathVariable Long feedId
    ){
        return feedService.getFeed(feedId);

    }

    @DeleteMapping("/feeds/{feedId}")
    public ResponseEntity<?> deleteFeed(
            @PathVariable Long feedId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        User user = userDetails.getUser();
        feedService.deleteFeed(feedId,user);
        return ResponseEntity.ok().body("피드 삭제 완료");
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFeed(@RequestParam String keyword){
        return feedService.searchFeed(keyword);
    }

}
