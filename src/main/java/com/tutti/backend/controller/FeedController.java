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
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final HeaderTokenExtractor headerTokenExtractor;
    private final JwtDecoder jwtDecoder;


    // 메인 페이지
    @GetMapping("/")
    public ResponseEntity<?> getMainPage(HttpServletRequest httpServletRequest) {
        String jwtToken = httpServletRequest.getHeader("Authorization");
        if (Objects.equals(jwtToken, "")) {
            return feedService.getMainPage();
        }
        return feedService.getMainPageByUser(
                jwtDecoder.decodeUsername(headerTokenExtractor.extract(jwtToken, httpServletRequest)));
    }




    // 최신 순 전체 피드 따로 가져오기
    @GetMapping("/feeds")
    public ResponseEntity<?> getFeedPage(@RequestParam String postType,@RequestParam(required = false) String genre){
        return feedService.getFeedPage(postType,genre);
    }
    // 장르 별 피드 따로 가져오기
    /*@GetMapping("/feeds/search")
    public ResponseEntity<?> getFeedByGenrePage(@RequestParam String genre){
        return feedService.getFeedByGenrePage(genre);
    }*/



    // 피드 작성
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

    // 피드 수정
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

    // 피드 상세 조회
    @GetMapping("/feeds/{feedId}")
    public ResponseEntity<?> getFeed(
            @PathVariable Long feedId,
            HttpServletRequest httpServletRequest
    ){
        return feedService.getFeed(feedId,httpServletRequest);

    }
    // 피드 삭제
    @DeleteMapping("/feeds/{feedId}")
    public ResponseEntity<?> deleteFeed(
            @PathVariable Long feedId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        User user = userDetails.getUser();
        feedService.deleteFeed(feedId,user);
        return ResponseEntity.ok().body("피드 삭제 완료");
    }
    // 피드 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchFeed(@RequestParam String keyword){
        return feedService.searchFeed(keyword);
    }
    @GetMapping("/search/more")
    public ResponseEntity<?> searchMoreFeed(@RequestParam String category,@RequestParam String keyword){
        return feedService.searchMoreFeed(keyword,category);
    }

}
