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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
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
    @PostMapping(value ="/user/signup", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public ResponseEntity<?> registerUser(@Valid @RequestPart SignupRequestDto signupData,
                                          @Nullable @RequestPart MultipartFile file) {
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
    @PostMapping("/follow")
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
    // 유저 정보 > 관심음악 조회
    @GetMapping("/user/mypage/hearts")
    public ResponseEntity<?> userLikeRead (@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserLike(userDetails);
    }

    // 유저 정보 > 관심영상 조회
    @GetMapping("/user/mypage/hearts/video")
    public ResponseEntity<?> userLikeVideoRead (@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserLikeVideo(userDetails);
    }
    // 유저 정보 > 팔로잉 조회
    @GetMapping("/user/mypage/follows")
    public ResponseEntity<?> userFollowRead (@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserFollow(userDetails);
    }
    // 유저 정보 > 업로드한 음악조회
    @GetMapping("/user/mypage/myfeed")
    public ResponseEntity<?> userMyFeedRead (@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserMyFeed(userDetails);
    }
    // 유저 정보 > 업로드한 영상조회
    @GetMapping("/user/mypage/myfeed/video")
    public ResponseEntity<?> userMyFeedVideoRead(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserMyFeedVideo(userDetails);
    }

    @GetMapping("/search/moreArtist")
    public ResponseEntity<?> searchMoreArtist(@RequestParam String keyword){
        return userService.searchMoreArtist(keyword);
    }

    // 유저 정보 수정(myPage)
    @PutMapping("/user/mypage")
    public ResponseEntity<?> updateUser(
            @RequestPart(required = false) MultipartFile file
            ,@RequestPart UserUpdateRequestDto updateData
            ,@AuthenticationPrincipal UserDetailsImpl userDetails){
        if(userDetails.getUser()==null){
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        User user = userDetails.getUser();
        if(file==null){
            userService.updateUserWithNoFile(updateData,user);
        }else{
            userService.updateUser(file,updateData,user);
        }
        return ResponseEntity.ok().body("마이 페이지 수정 완료");
    }

    // 다른 사람 프로필 조회
    // 로그인 Artist는 로컬스토리지에 저장해서 RequestBody로 보내준다는 가정하에 작성함.
    @GetMapping("/user/profile/{artist}")
    public ResponseEntity<?> othersUser(
            @PathVariable String artist,
            HttpServletRequest httpServletRequest) {
        return userService.getOthersUser(artist,httpServletRequest);
    }
    // 타인 정보 > 관심음악 조회
    @GetMapping("/user/profile/{artist}/hearts")
    public ResponseEntity<?> othersUserLikeRead(
            @PathVariable String artist,
            HttpServletRequest httpServletRequest) {
        return userService.getOthersUserLike(artist,httpServletRequest);
    }
    // 타인 정보 > 관심영상 조회
    @GetMapping("/user/profile/{artist}/hearts/video")
    public ResponseEntity<?> othersUserLikeVideoRead(
            @PathVariable String artist,
            HttpServletRequest httpServletRequest) {
        return userService.getOthersUserLikeVideo(artist,httpServletRequest);
    }
    // 타인 정보 > 팔로잉 조회
    @GetMapping("/user/profile/{artist}/follows")
    public ResponseEntity<?> othersUserFollowRead(
            @PathVariable String artist,
            HttpServletRequest httpServletRequest) {
        return userService.getOthersUserFollow(artist,httpServletRequest);
    }
    // 타인 정보 > 업로드한 음악조회
    @GetMapping("/user/profile/{artist}/feeds")
    public ResponseEntity<?> othersUserFeedRead
    (@PathVariable String artist,
     HttpServletRequest httpServletRequest) {
        return userService.getOthersUserFeed(artist,httpServletRequest);
    }
    // 타인 정보 > 업로드한 영상조회
    @GetMapping("/user/profile/{artist}/feeds/video")
    public ResponseEntity<?> othersUserFeedVideoRead(
            @PathVariable String artist,
            HttpServletRequest httpServletRequest) {
        return userService.getOthersUserFeedVideo(artist,httpServletRequest);
    }

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public ResponseEntity<KakaoUserResponseDto> kakaoLogin(
            @RequestParam String code,
            HttpServletResponse response) throws JsonProcessingException {
        log.info("1");
        KakaoUserResponseDto kakaoUserResponseDto = kakaoUserService.kakaoLogin(code, response);
        log.info("21");
        return ResponseEntity.ok().body(kakaoUserResponseDto);
    }
    //https://kauth.kakao.com/oauth/authorize?client_id=346b2f15b0bcf829529a506449139680&redirect_uri=https://seyeolpersonnal.shop/user/kakao/callback&response_type=code

    //https://kauth.kakao.com/oauth/authorize?client_id=346b2f15b0bcf829529a506449139680&redirect_uri=https://localhost:3000/oauth/kakao/callback/kakao/callback&response_type=code

    // 구글 로그인
    @GetMapping("/api/user/google/callback")
    public GoogleResponseDto<GoogleUserResponseDto> googleLogin(
            @RequestParam String code,
            HttpServletResponse httpServletResponse) throws JsonProcessingException {
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
