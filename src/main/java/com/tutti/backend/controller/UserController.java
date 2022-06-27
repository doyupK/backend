package com.tutti.backend.controller;

import com.tutti.backend.dto.user.SignupRequestDto;
import com.tutti.backend.dto.user.request.ArtistRequestDto;
import com.tutti.backend.dto.user.request.EmailRequestDto;
import com.tutti.backend.dto.user.request.FollowRequestDto;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;

    //    USER service DI
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    // 회원 가입 요청 처리
    @PostMapping(value ="/user/signup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> registerUser(@Valid @RequestPart SignupRequestDto signupData, @RequestPart MultipartFile file) {
        return userService.registerUser(signupData, file);
    }

    @PostMapping("/user/email")
    public ResponseEntity<?> emailCheck(@RequestBody EmailRequestDto emailRequestDto){
        return userService.getUserEmailCheck(emailRequestDto);
    }
    @PostMapping("/user/artist")
    public ResponseEntity<?> artistCheck(@RequestBody ArtistRequestDto artistRequestDto){
        return userService.getUserArtistCheck(artistRequestDto);
    }

//    @GetMapping("/user/mypage")
//    public ResponseEntity<?> getUserData(@AuthenticationPrincipal UserDetailsImpl userDetails){
//        return userService.getUserDetail(userDetails);
//    }


    @GetMapping("/follow")
    public ResponseEntity<?> followArtist(
                                    @RequestParam(required = false) String artist,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.followArtist(artist, userDetails);
    }

}
