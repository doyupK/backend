package com.tutti.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.google.GoogleUserResponseDto;
import com.tutti.backend.dto.user.GoogleResponseDto;
import com.tutti.backend.dto.user.KakaoUserResponseDto;
import com.tutti.backend.dto.user.SignupRequestDto;
import com.tutti.backend.dto.user.request.ArtistRequestDto;
import com.tutti.backend.dto.user.request.EmailRequestDto;
import com.tutti.backend.dto.user.request.UserUpdateRequestDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.GoogleUserService;
import com.tutti.backend.service.KakaoUserService;
import com.tutti.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;

    private final KakaoUserService kakaoUserService;

    private final GoogleUserService googleUserService;





    //    USER service DI
    @Autowired
    public UserController(UserService userService,
                          KakaoUserService kakaoUserService,
                          GoogleUserService googleUserService) {

        this.userService = userService;
        this.kakaoUserService = kakaoUserService;
        this.googleUserService = googleUserService;

    }


    // 회원 가입 요청 처리
    @PostMapping(value ="/user/signup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> registerUser(@Valid @RequestPart SignupRequestDto signupData, @RequestPart MultipartFile file) {
        return userService.registerUser(signupData, file);
    }
    // 이메일 중복 검사
    @PostMapping("/user/email")
    public ResponseEntity<?> emailCheck(@RequestBody EmailRequestDto emailRequestDto){
        return userService.getUserEmailCheck(emailRequestDto);
    }
    // 닉네임(Artist) 중복 검사
    @PostMapping("/user/artist")
    public ResponseEntity<?> artistCheck(@RequestBody ArtistRequestDto artistRequestDto){
        return userService.getUserArtistCheck(artistRequestDto);
    }


    // 팔로우
    @GetMapping("/follow")
    public ResponseEntity<?> followArtist(
                                    @RequestParam(required = false) String artist,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.followArtist(artist, userDetails);
    }
    // 유저 정보 조회(myPage)
    @GetMapping("/user/mypage")
    public ResponseEntity<?> infoRead (@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserInfo(userDetails);
    }
    // 유저 정보 수정(myPage)
    @PutMapping("/user/mypage")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequestDto userUpdateRequestDto,@AuthenticationPrincipal UserDetailsImpl userDetails){
        if(userDetails.getUser()==null){
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        User user = userDetails.getUser();
        userService.updateUser(userUpdateRequestDto,user);
        return ResponseEntity.ok().body("마이 페이지 수정 완료");
    }

    // 다른 사람 프로필 조회
    // 로그인 Artist는 로컬스토리지에 저장해서 RequestBody로 보내준다는 가정하에 작성함.
    @GetMapping("/user/profile/{artist}")
    public ResponseEntity<?> othersUser(@PathVariable String artist, HttpServletRequest httpServletRequest) {

        return userService.getOthersUser(artist,httpServletRequest);
    }
    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public ResponseEntity<KakaoUserResponseDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        KakaoUserResponseDto kakaoUserResponseDto = kakaoUserService.kakaoLogin(code, response);
        return ResponseEntity.ok().body(kakaoUserResponseDto);
    }
    //https://kauth.kakao.com/oauth/authorize?client_id=346b2f15b0bcf829529a506449139680&redirect_uri=http://localhost:8080/user/kakao/callback&response_type=code

    // 구글 로그인
    @GetMapping("/api/user/google/callback")
    public GoogleResponseDto<GoogleUserResponseDto> googleLogin(@RequestParam String code, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        GoogleUserResponseDto googleUserResponseDto = googleUserService.googleLogin(code);
        httpServletResponse.addHeader("Authorization", googleUserResponseDto.getToken());
        return GoogleResponseDto.<GoogleUserResponseDto>builder()
                .status(HttpStatus.OK.toString())
                .message("구글 소셜 로그인 요청")
                .data(googleUserResponseDto)
                .build();
    }

    //https://accounts.google.com/o/oauth2/v2/auth?client_id=693007930110-5u66i5c992figci0e3oh3n53sm8q6hb7.apps.googleusercontent.com&redirect_uri=http://localhost:8080/api/user/google/callback&response_type=code&scope=email%20profile%20openid&access_type=offline





}
