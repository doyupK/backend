package com.tutti.backend.service;


import com.tutti.backend.domain.*;
import com.tutti.backend.dto.Feed.MainPageFeedDto;
import com.tutti.backend.dto.Feed.UserinfoResponseFeedDto;
import com.tutti.backend.dto.user.*;
import com.tutti.backend.dto.user.request.ArtistRequestDto;
import com.tutti.backend.dto.user.request.EmailRequestDto;
import com.tutti.backend.dto.user.request.UserUpdateRequestDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.dto.user.response.UserInfoDto;
import com.tutti.backend.dto.user.response.UserInfoResponseDto;
import com.tutti.backend.repository.FollowRepository;
import com.tutti.backend.repository.HeartRepository;
import com.tutti.backend.repository.UserRepository;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.security.jwt.HeaderTokenExtractor;
import com.tutti.backend.security.jwt.JwtDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UserService {

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final HeartRepository heartRepository;
    private final HeaderTokenExtractor headerTokenExtractor;
    private final JwtDecoder jwtDecoder;


    @Autowired
    public UserService(S3Service s3Service,
                       UserRepository userRepository,
                       FollowRepository followRepository,
                       PasswordEncoder passwordEncoder,
                       ConfirmationTokenService confirmationTokenService,
                       HeartRepository heartRepository,
                       HeaderTokenExtractor headerTokenExtractor,
                       JwtDecoder jwtDecoder) {
        this.s3Service = s3Service;
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
        this.heartRepository = heartRepository;
        this.headerTokenExtractor = headerTokenExtractor;
        this.jwtDecoder = jwtDecoder;
    }

    @Transactional
    public ResponseEntity<?> registerUser(SignupRequestDto signupRequestDto, MultipartFile file) {
        ResponseDto signupResponseDto = new ResponseDto();
        Optional<User> findUser = userRepository.findByEmail(signupRequestDto.getEmail());
        if(findUser.isPresent()){
            throw new CustomException(ErrorCode.EXIST_EMAIL);
        }
        FileRequestDto fileRequestDto = s3Service.upload(file);
//      PW Hash
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        User user = new User(signupRequestDto, password, fileRequestDto);
//      Email 전송
        confirmationTokenService.createEmailConfirmationToken(signupRequestDto.getEmail());
//      DB 저장
        userRepository.save(user);

        signupResponseDto.setSuccess(200);
        signupResponseDto.setMessage("회원가입 성공");
        return ResponseEntity.ok().body(signupResponseDto);
    }

    public ResponseEntity<?> getUserEmailCheck(EmailRequestDto emailRequestDto) {
        ResponseDto signupResponseDto = new ResponseDto();
        Optional<User> user = userRepository.findByEmail(emailRequestDto.getEmail());
        if(user.isPresent()){
            throw new CustomException(ErrorCode.EXIST_EMAIL);
        }

        signupResponseDto.setSuccess(200);
        signupResponseDto.setMessage("사용할 수 있는 이메일입니다.");
        return ResponseEntity.ok().body(signupResponseDto);
    }

    public ResponseEntity<?> getUserArtistCheck(ArtistRequestDto artistRequestDto) {
        ResponseDto signupResponseDto = new ResponseDto();
        Optional<User> user = userRepository.findByArtist(artistRequestDto.getArtist());
        if(user.isPresent()){
            throw new CustomException(ErrorCode.EXIST_ARTIST);
        }

        signupResponseDto.setSuccess(200);
        signupResponseDto.setMessage("사용할 수 있는 아티스트명입니다.");
        return ResponseEntity.ok().body(signupResponseDto);
    }

    @Transactional
    public void confirmEmail(String token) {
        ConfirmationToken findConfirmationToken = confirmationTokenService.findByIdAndExpirationDateAfterAndExpired(token);
        Optional<User> findUserInfo = userRepository.findByEmail(findConfirmationToken.getUserEmail());
        findConfirmationToken.useToken();    // 토큰 만료

        if (!findUserInfo.isPresent()) {
            throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
        }

//      User Confirm 정보 'OK' 로 변경
        findUserInfo.get().setUserConfirmEnum(UserConfirmEnum.OK_CONFIRM);
    }

    public ResponseEntity<?> followArtist(String artist, UserDetailsImpl userDetails) {
        ResponseDto responseDto = new ResponseDto();
        //로그인 정보에서 User객체 추출
        Optional<User> findLoginUser = userRepository.findByEmail(userDetails.getUser().getEmail());
        //artist User 객체 추출
        Optional<User> findArtist = userRepository.findByArtist(artist);

        if(!findLoginUser.isPresent()){
            throw new CustomException(ErrorCode.WRONG_USER);
        }
        if(!findArtist.isPresent()){
            throw new CustomException(ErrorCode.NOT_EXISTS_USERNAME);
        }

        User user = findLoginUser.get();
        User findArtistResult = findArtist.get();
        Follow follow = new Follow(user, findArtistResult);

        followRepository.save(follow);

        responseDto.setSuccess(200);
        responseDto.setMessage("완료!");

        return ResponseEntity.ok().body(responseDto);
    }
    @Transactional
    public void updateUser(UserUpdateRequestDto userUpdateRequestDto, User currentUser) {
        User user = userRepository.findById(currentUser.getId()).orElseThrow(()->new CustomException(ErrorCode.WRONG_USER));

        user.updateUser(userUpdateRequestDto);

    }

    @Transactional
    public ResponseEntity<?> getUserInfo(UserDetailsImpl userDetails) {
        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
        UserinfoResponseFeedDto userinfoResponseFeedDto = new UserinfoResponseFeedDto();

        User user = userDetails.getUser();
        Long followingCount = followRepository.countByUser(user);
        Long followerCount = followRepository.countByFollowingUser(user);
        String[] genre = {
                user.getFavoriteGenre1(),
                user.getFavoriteGenre2(),
                user.getFavoriteGenre3(),
                user.getFavoriteGenre4()
        };
        List<Heart> heartList = heartRepository.findAllByUserAndIsHeartTrue(user);

        List<MainPageFeedDto> likeListDto = new ArrayList<>();


        UserInfoDto userInfoDto = new UserInfoDto(user.getArtist()
                , genre, user.getProfileUrl(), followerCount,
                followingCount, user.getProfileText(),
                user.getInstagramUrl(), user.getYoutubeUrl());
        for(Heart heart : heartList) {
            MainPageFeedDto mainPageFeedDto = new MainPageFeedDto(heart.getFeed(),user);
            likeListDto.add(mainPageFeedDto);
        }

        List<FollowingDtoMapping> followingList = followRepository.findByUser(user);

        userinfoResponseFeedDto.setUserInfoDto(userInfoDto);
        userinfoResponseFeedDto.setLikeList(likeListDto);
        userinfoResponseFeedDto.setFollowingList(followingList);
        userInfoResponseDto.setSuccess(200);
        userInfoResponseDto.setMessage("성공");
        userInfoResponseDto.setData(userinfoResponseFeedDto);
        return ResponseEntity.ok().body(userInfoResponseDto);
    }

    public ResponseEntity<?> getOthersUser(String artist, HttpServletRequest httpServletRequest) {

        String jwtToken = httpServletRequest.getHeader("Authorization");
        if(jwtToken!=null){
            // user 찾기
            String userEmail = jwtDecoder.decodeUsername(headerTokenExtractor.extract(jwtToken, httpServletRequest));
            User findUser = userRepository.findByEmail(userEmail).orElseThrow(
                    ()->new CustomException(ErrorCode.NOT_FOUND_USER)
            );

            if(findUser.getArtist().equals(artist)) {
                throw new CustomException(ErrorCode.MOVED_TEMPORARILY);
            }

        }
        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
        UserinfoResponseFeedDto userinfoResponseFeedDto = new UserinfoResponseFeedDto();

        User user = userRepository.findByArtist(artist).orElseThrow(
                ()-> new CustomException(ErrorCode.TEMPORARY_SERVER_ERROR) // 커스텀 에러로 바꿀거
        );


        Long followingCount = followRepository.countByUser(user);
        Long followerCount = followRepository.countByFollowingUser(user);
        String[] genre = {
                user.getFavoriteGenre1(),
                user.getFavoriteGenre2(),
                user.getFavoriteGenre3(),
                user.getFavoriteGenre4()
        };
        List<Heart> heartList = heartRepository.findAllByUserAndIsHeartTrue(user);

        List<MainPageFeedDto> likeListDto = new ArrayList<>();


        UserInfoDto userInfoDto = new UserInfoDto(user.getArtist()
                , genre, user.getProfileUrl(), followerCount,
                followingCount, user.getProfileText(),
                user.getInstagramUrl(), user.getYoutubeUrl());
        for(Heart heart : heartList) {
            MainPageFeedDto mainPageFeedDto = new MainPageFeedDto(heart.getFeed(),user);
            likeListDto.add(mainPageFeedDto);
        }

        List<FollowingDtoMapping> followingList = followRepository.findByUser(user);

        userinfoResponseFeedDto.setUserInfoDto(userInfoDto);
        userinfoResponseFeedDto.setLikeList(likeListDto);
        userinfoResponseFeedDto.setFollowingList(followingList);
        userInfoResponseDto.setSuccess(200);
        userInfoResponseDto.setMessage("성공");
        userInfoResponseDto.setData(userinfoResponseFeedDto);

        return ResponseEntity.ok().body(userInfoResponseDto);
    }
}
