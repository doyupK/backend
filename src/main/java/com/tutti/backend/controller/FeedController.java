package com.tutti.backend.controller;

import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.FeedRequestDto;
import com.tutti.backend.dto.Feed.FeedUpdateRequestDto;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.security.jwt.HeaderTokenExtractor;
import com.tutti.backend.security.jwt.JwtDecoder;
import com.tutti.backend.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final HeaderTokenExtractor headerTokenExtractor;
    private final JwtDecoder jwtDecoder;



    @GetMapping("/")
    public ResponseEntity<?> getMainPage(HttpServletRequest httpServletRequest) {
        String jwtToken = httpServletRequest.getHeader("Authorization");
        if (jwtToken == null) {
            return feedService.getMainPage();
        }
        return ResponseEntity.ok().body(feedService.getMainPageByUser(
                jwtDecoder.decodeUsername(headerTokenExtractor.extract(jwtToken, httpServletRequest))));
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
