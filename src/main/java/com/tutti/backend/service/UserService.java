package com.tutti.backend.service;


import com.tutti.backend.domain.*;
import com.tutti.backend.dto.Feed.UserinfoResponseFeedDto;
import com.tutti.backend.dto.user.*;
import com.tutti.backend.dto.user.request.ArtistRequestDto;
import com.tutti.backend.dto.user.request.EmailRequestDto;
import com.tutti.backend.dto.user.request.UserUpdateRequestDto;
import com.tutti.backend.dto.user.response.UserDataResponseDto;
import com.tutti.backend.dto.user.response.UserFollowDataResponseDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.dto.user.response.UserInfoDto;
import com.tutti.backend.dto.user.response.UserInfoResponseDto;
import com.tutti.backend.repository.FeedRepository;
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
import java.util.Objects;
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
    private final FeedRepository feedRepository;


    @Autowired
    public UserService(S3Service s3Service,
                       UserRepository userRepository,
                       FollowRepository followRepository,
                       PasswordEncoder passwordEncoder,
                       ConfirmationTokenService confirmationTokenService,
                       HeartRepository heartRepository,
                       HeaderTokenExtractor headerTokenExtractor,
                       JwtDecoder jwtDecoder,
                       FeedRepository feedRepository) {
        this.s3Service = s3Service;
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
        this.heartRepository = heartRepository;
        this.headerTokenExtractor = headerTokenExtractor;
        this.jwtDecoder = jwtDecoder;
        this.feedRepository = feedRepository;
    }

    // 회원가입
    @Transactional
    public ResponseEntity<?> registerUser(SignupRequestDto signupRequestDto, MultipartFile file) {
        ResponseDto signupResponseDto = new ResponseDto();
        // 유저 이메일 조회 후 이미 존재하면 예외처리
        Optional<User> findUser = userRepository.findByEmail(signupRequestDto.getEmail());
        if(findUser.isPresent()){
            throw new CustomException(ErrorCode.EXIST_EMAIL);
        }
        // 프로필 이미지 업로드
        FileRequestDto fileRequestDto = s3Service.upload(file);
//      PW Hash
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        User user = new User(signupRequestDto, password, fileRequestDto);
//      Email 전송 (비동기 함수)
        confirmationTokenService.createEmailConfirmationToken(signupRequestDto.getEmail());
//      DB 저장
        userRepository.save(user);

        signupResponseDto.setSuccess(200);
        signupResponseDto.setMessage("회원가입 성공");
        return ResponseEntity.ok().body(signupResponseDto);
    }
    // 이메일 중복 검사
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
    // 닉네임(Artist) 중복 검사
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
    // 이메일 인증
    @Transactional
    public void confirmEmail(String token) {
        ConfirmationToken findConfirmationToken = confirmationTokenService
                .findByIdAndExpirationDateAfterAndExpired(token);
        Optional<User> findUserInfo = userRepository.findByEmail(findConfirmationToken.getUserEmail());
        findConfirmationToken.useToken();    // 토큰 만료

        if (!findUserInfo.isPresent()) {
            throw new CustomException(ErrorCode.NOT_FOUND_TOKEN);
        }

        // User Confirm 정보 'OK' 로 변경
        findUserInfo.get().setUserConfirmEnum(UserConfirmEnum.OK_CONFIRM);
    }
    // 팔로잉
    public ResponseEntity<?> followArtist(String artist, UserDetailsImpl userDetails) {
        ResponseDto responseDto = new ResponseDto();
//        // 로그인 정보에서 User객체 추출 (로그인 유저)
//        Optional<User> findLoginUser = userRepository.findByEmail(userDetails.getUser().getEmail());
//        // artist User 객체 추출 (로그인 유저가 팔로우 할 Artist)
//        Optional<User> findArtist = userRepository.findByArtist(artist);
//
//        if(!findLoginUser.isPresent()){
//            throw new CustomException(ErrorCode.WRONG_USER);
//        }
//        if(!findArtist.isPresent()){
//            throw new CustomException(ErrorCode.NOT_EXISTS_USERNAME);
//        }
//
//        User user = findLoginUser.get();
//        User findArtistResult = findArtist.get();
//        Follow follow = new Follow(user, findArtistResult);
//
//        followRepository.save(follow);
        Follow follow = followRepository.findByUserAndFollowingUser_Artist(userDetails.getUser(), artist);
        if (follow != null) {
            followRepository.delete(follow);
        } else {
            Optional<User> otherUser = userRepository.findByArtist(artist);
            if(!otherUser.isPresent()){
                throw new CustomException(ErrorCode.NOT_EXISTS_USERNAME);
            }
            followRepository.save(new Follow(userDetails.getUser(), otherUser.get()));
        }
        responseDto.setSuccess(200);
        responseDto.setMessage("완료!");

        return ResponseEntity.ok().body(responseDto);
    }

    // 유저 정보 수정(myPage)
    @Transactional
    public void updateUser(MultipartFile file, UserUpdateRequestDto userUpdateRequestDto, User currentUser) {
        User user = userRepository.findById(
                currentUser.getId()).orElseThrow(
                        ()->new CustomException(ErrorCode.WRONG_USER));
        if(file.isEmpty()){
            user.updateUser(userUpdateRequestDto);
        }else{
            s3Service.deleteImageUrl(user.getProfileUrl());
            FileRequestDto fileRequestDto = s3Service.upload(file);
            user.updateUser(fileRequestDto,userUpdateRequestDto);
        }
    }
    @Transactional
    public void updateUserWithNoFile(UserUpdateRequestDto userUpdateRequestDto, User currentUser) {
        User user = userRepository.findById(
                currentUser.getId()).orElseThrow(
                ()->new CustomException(ErrorCode.WRONG_USER));
        user.updateUser(userUpdateRequestDto);
    }


    // 유저 정보 조회(myPage)
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
        boolean[] genreSelected = {
                user.isGenreSelected1(),
                user.isGenreSelected2(),
                user.isGenreSelected3(),
                user.isGenreSelected4(),
                user.isGenreSelected5(),
                user.isGenreSelected6()
        };

        UserInfoDto userInfoDto = new UserInfoDto(
                user.getArtist(),
                genre,
                genreSelected,
                user.getProfileUrl(),
                followerCount,
                followingCount,
                user.getProfileText(),
                user.getInstagramUrl(),
                user.getYoutubeUrl());

        userinfoResponseFeedDto.setUserInfoDto(userInfoDto);
        userinfoResponseFeedDto.setLikeList(feedRepository
                .findTop6ByPostTypeAndHearts_UserAndHearts_IsHeartTrueOrderByHearts_IdDesc(
                        "audio",user)); // 유저의 좋아요[음악] List를 최신순으로 6개만 가져오기
        userinfoResponseFeedDto.setLikeVideoList(feedRepository
                .findTop6ByPostTypeAndHearts_UserAndHearts_IsHeartTrueOrderByHearts_IdDesc(
                        "video",user)); // 유저의 좋아요[영상] List를 최신순으로 6개만 가져오기
        userinfoResponseFeedDto.setFollowingList(followRepository
                .findTop7ByUserOrderById(user)); // 유저가 팔로우 한 리스트 7개
        userinfoResponseFeedDto.setUploadList(feedRepository
                .findTop6ByPostTypeAndUserOrderByIdDesc(
                        "audio",user)); // 유저가 업로드[노래] 한 리스트 6개
        userinfoResponseFeedDto.setUploadVideoList(feedRepository
                .findTop6ByPostTypeAndUserOrderByIdDesc(
                        "video",user)); // 유저가 업로드[영상] 한 리스트 6개
        userInfoResponseDto.setSuccess(200);
        userInfoResponseDto.setMessage("성공");
        userInfoResponseDto.setData(userinfoResponseFeedDto);
        return ResponseEntity.ok().body(userInfoResponseDto);
    }
    // 다른 사람 프로필 조회
    public ResponseEntity<?> getOthersUser(String artist, HttpServletRequest httpServletRequest) {
        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
        UserinfoResponseFeedDto userinfoResponseFeedDto = new UserinfoResponseFeedDto();

        String jwtToken = httpServletRequest.getHeader("Authorization");
        Boolean isFollow = false;
        if(!Objects.equals(jwtToken, "")){
            // 현재 로그인 한 user 찾기
            String userEmail = jwtDecoder.decodeUsername(headerTokenExtractor.extract(jwtToken, httpServletRequest));
            User findUser = userRepository.findByEmail(userEmail).orElseThrow(
                    ()->new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            // 현재 로그인 유저의 아티스트 이름과 request artist 가 같으면 같은 사람이므로 예외처리
            if(findUser.getArtist().equals(artist)) {
                throw new CustomException(ErrorCode.MOVED_TEMPORARILY);
            }
            // 로그인 한 사람의 follow flag bit
            isFollow = followRepository.existsByUserAndFollowingUser_Artist(findUser, artist);
        }
        

        User user = userRepository.findByArtist(artist).orElseThrow(
                ()-> new CustomException(ErrorCode.TEMPORARY_SERVER_ERROR)
        );

        Long followingCount = followRepository.countByUser(user);
        Long followerCount = followRepository.countByFollowingUser(user);
        String[] genre = {
                user.getFavoriteGenre1(),
                user.getFavoriteGenre2(),
                user.getFavoriteGenre3(),
                user.getFavoriteGenre4()
        };
//        List<Heart> heartList = heartRepository.findAllByUserAndIsHeartTrue(user);
//
//        List<MainPageFeedDto> likeListDto = new ArrayList<>();


        UserInfoDto userInfoDto = new UserInfoDto(
                user.getArtist(),
                genre,
                null,
                user.getProfileUrl(),
                followerCount,
                followingCount,
                user.getProfileText(),
                user.getInstagramUrl(),
                user.getYoutubeUrl());
//        for(Heart heart : heartList) {
//            MainPageFeedDto mainPageFeedDto = new MainPageFeedDto(heart.getFeed(),user);
//            likeListDto.add(mainPageFeedDto);
//        }

//        List<FollowingDtoMapping> followingList = followRepository.findByUser(user);

        userinfoResponseFeedDto.setUserInfoDto(userInfoDto);
        userinfoResponseFeedDto.setLikeList(
                feedRepository.findTop6ByPostTypeAndHearts_UserAndHearts_IsHeartTrueOrderByHearts_IdDesc(
                        "audio",user)); // 유저의 좋아요[음악] List를 최신순으로 6개만 가져오기
        userinfoResponseFeedDto.setLikeVideoList(
                feedRepository.findTop6ByPostTypeAndHearts_UserAndHearts_IsHeartTrueOrderByHearts_IdDesc(
                        "video",user)); // 유저의 좋아요[영상] List를 최신순으로 6개만 가져오기
        userinfoResponseFeedDto.setFollowingList(
                followRepository.findTop7ByUserOrderById(user)); // 유저가 팔로우 한 리스트 7개
        userinfoResponseFeedDto.setUploadList(
                feedRepository.findTop6ByPostTypeAndUserOrderByIdDesc(
                        "audio",user)); // 유저가 업로드[노래] 한 리스트 6개
        userinfoResponseFeedDto.setUploadVideoList(
                feedRepository.findTop6ByPostTypeAndUserOrderByIdDesc(
                        "video",user)); // 유저가 업로드[영상] 한 리스트 6개
        userInfoResponseDto.setSuccess(200);
        userInfoResponseDto.setMessage("성공");
        userInfoResponseDto.setIsFollow(isFollow);
        userInfoResponseDto.setData(userinfoResponseFeedDto);

        return ResponseEntity.ok().body(userInfoResponseDto);
    }

    // 유저 정보 > 관심음악 조회
    public ResponseEntity<?> getUserLike(UserDetailsImpl userDetails) {
        UserDataResponseDto userDataResponseDto = new UserDataResponseDto();
        User user = userDetails.getUser();
        // 유저의 좋아요[노래] List를 최신순으로 전체 가져오기
        userDataResponseDto.setData(feedRepository
                .findAllByPostTypeAndHearts_User_ArtistAndHearts_IsHeartTrueOrderByHearts_IdDesc(
                        "audio",user.getArtist()));
        userDataResponseDto.setMessage("성공");
        userDataResponseDto.setSuccess(200);

        return ResponseEntity.ok().body(userDataResponseDto);
    }

    // 유저 정보 > 팔로잉 조회
    public ResponseEntity<?> getUserFollow(UserDetailsImpl userDetails) {
        UserFollowDataResponseDto userFollowDataResponseDto = new UserFollowDataResponseDto();
        // 유저의 팔로잉 List를 최신순으로 전체 가져오기
        userFollowDataResponseDto.setData(followRepository.findAllByUser_Artist(userDetails.getUser().getArtist()));
        userFollowDataResponseDto.setMessage("성공");
        userFollowDataResponseDto.setSuccess(200);

        return ResponseEntity.ok().body(userFollowDataResponseDto);
    }

    // 유저 정보 > 업로드한 음악조회
    public ResponseEntity<?> getUserMyFeed(UserDetailsImpl userDetails) {
        UserDataResponseDto userDataResponseDto = new UserDataResponseDto();
        // 유저의 업로드[노래] List를 최신순으로 전체 가져오기
        userDataResponseDto.setData(feedRepository
                .findAllByPostTypeAndUser_ArtistOrderByIdDesc(
                        "audio",userDetails.getUser().getArtist()));
        userDataResponseDto.setMessage("성공");
        userDataResponseDto.setSuccess(200);

        return ResponseEntity.ok().body(userDataResponseDto);
    }

    // 타인 정보 > 관심음악 조회
    public ResponseEntity<?> getOthersUserLike(String artist, HttpServletRequest httpServletRequest) {
        UserDataResponseDto userDataResponseDto = new UserDataResponseDto();

        String jwtToken = httpServletRequest.getHeader("Authorization");
        if(!Objects.equals(jwtToken, "")){
            // 현재 로그인 한 user 찾기
            String userEmail = jwtDecoder.decodeUsername(headerTokenExtractor.extract(jwtToken, httpServletRequest));
            User findUser = userRepository.findByEmail(userEmail).orElseThrow(
                    ()->new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            // 현재 로그인 유저의 아티스트 이름과 request artist 가 같으면 같은 사람이므로 예외처리
            if(findUser.getArtist().equals(artist)) {
                throw new CustomException(ErrorCode.MOVED_TEMPORARILY);
            }
        }
        // 유저의 좋아요[노래] List를 최신순으로 전체 가져오기
        userDataResponseDto.setData(feedRepository
                .findAllByPostTypeAndHearts_User_ArtistAndHearts_IsHeartTrueOrderByHearts_IdDesc(
                        "audio",artist));
        userDataResponseDto.setMessage("성공");
        userDataResponseDto.setSuccess(200);


        return ResponseEntity.ok().body(userDataResponseDto);
    }

    // 타인 정보 > 팔로잉 조회
    public ResponseEntity<?> getOthersUserFollow(String artist, HttpServletRequest httpServletRequest) {
        UserFollowDataResponseDto userFollowDataResponseDto = new UserFollowDataResponseDto();

        String jwtToken = httpServletRequest.getHeader("Authorization");
        if(!Objects.equals(jwtToken, "")){
            // 현재 로그인 한 user 찾기
            String userEmail = jwtDecoder.decodeUsername(headerTokenExtractor.extract(jwtToken, httpServletRequest));
            User findUser = userRepository.findByEmail(userEmail).orElseThrow(
                    ()->new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            // 현재 로그인 유저의 아티스트 이름과 request artist 가 같으면 같은 사람이므로 예외처리
            if(findUser.getArtist().equals(artist)) {
                throw new CustomException(ErrorCode.MOVED_TEMPORARILY);
            }
        }
        // 유저의 팔로잉 List를 최신순으로 전체 가져오기
        userFollowDataResponseDto.setData(followRepository.findAllByUser_Artist(artist));
        userFollowDataResponseDto.setMessage("성공");
        userFollowDataResponseDto.setSuccess(200);

        return ResponseEntity.ok().body(userFollowDataResponseDto);
    }

    // 타인 정보 > 업로드한 음악조회
    public ResponseEntity<?> getOthersUserFeed(String artist, HttpServletRequest httpServletRequest) {
        UserDataResponseDto userDataResponseDto = new UserDataResponseDto();

        String jwtToken = httpServletRequest.getHeader("Authorization");
        if(!Objects.equals(jwtToken, "")){
            // 현재 로그인 한 user 찾기
            String userEmail = jwtDecoder.decodeUsername(headerTokenExtractor.extract(jwtToken, httpServletRequest));
            User findUser = userRepository.findByEmail(userEmail).orElseThrow(
                    ()->new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            // 현재 로그인 유저의 아티스트 이름과 request artist 가 같으면 같은 사람이므로 예외처리
            if(findUser.getArtist().equals(artist)) {
                throw new CustomException(ErrorCode.MOVED_TEMPORARILY);
            }
        }
        // 유저의 업로드[노래] List를 최신순으로 전체 가져오기
        userDataResponseDto.setData(feedRepository.findAllByPostTypeAndUser_ArtistOrderByIdDesc("audio",artist));
        userDataResponseDto.setMessage("성공");
        userDataResponseDto.setSuccess(200);


        return ResponseEntity.ok().body(userDataResponseDto);
    }

    // 유저 정보 > 관심영상 조회
    public ResponseEntity<?> getUserLikeVideo(UserDetailsImpl userDetails) {
        UserDataResponseDto userDataResponseDto = new UserDataResponseDto();
        User user = userDetails.getUser();
        // 유저의 좋아요[영상] List를 최신순으로 전체 가져오기
        userDataResponseDto.setData(feedRepository.findAllByPostTypeAndHearts_User_ArtistAndHearts_IsHeartTrueOrderByHearts_IdDesc("video",user.getArtist()));
        userDataResponseDto.setMessage("성공");
        userDataResponseDto.setSuccess(200);

        return ResponseEntity.ok().body(userDataResponseDto);
    }
    // 유저 정보 > 업로드한 영상조회
    public ResponseEntity<?> getUserMyFeedVideo(UserDetailsImpl userDetails) {
        UserDataResponseDto userDataResponseDto = new UserDataResponseDto();
        // 유저의 업로드[영상] List를 최신순으로 전체 가져오기
        userDataResponseDto.setData(feedRepository.findAllByPostTypeAndUser_ArtistOrderByIdDesc("video",userDetails.getUser().getArtist()));
        userDataResponseDto.setMessage("성공");
        userDataResponseDto.setSuccess(200);

        return ResponseEntity.ok().body(userDataResponseDto);
    }
    // 타인 정보 > 관심영상 조회
    public ResponseEntity<?> getOthersUserLikeVideo(String artist, HttpServletRequest httpServletRequest) {
        UserDataResponseDto userDataResponseDto = new UserDataResponseDto();

        String jwtToken = httpServletRequest.getHeader("Authorization");
        if(!Objects.equals(jwtToken, "")){
            // 현재 로그인 한 user 찾기
            String userEmail = jwtDecoder.decodeUsername(headerTokenExtractor.extract(jwtToken, httpServletRequest));
            User findUser = userRepository.findByEmail(userEmail).orElseThrow(
                    ()->new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            // 현재 로그인 유저의 아티스트 이름과 request artist 가 같으면 같은 사람이므로 예외처리
            if(findUser.getArtist().equals(artist)) {
                throw new CustomException(ErrorCode.MOVED_TEMPORARILY);
            }
        }
        // 유저의 좋아요[영상] List를 최신순으로 전체 가져오기
        userDataResponseDto.setData(feedRepository.findAllByPostTypeAndHearts_User_ArtistAndHearts_IsHeartTrueOrderByHearts_IdDesc("video",artist));
        userDataResponseDto.setMessage("성공");
        userDataResponseDto.setSuccess(200);


        return ResponseEntity.ok().body(userDataResponseDto);
    }
    // 타인 정보 > 업로드한 영상조회
    public ResponseEntity<?> getOthersUserFeedVideo(String artist, HttpServletRequest httpServletRequest) {
        UserDataResponseDto userDataResponseDto = new UserDataResponseDto();

        String jwtToken = httpServletRequest.getHeader("Authorization");
        if(!Objects.equals(jwtToken, "")){
            // 현재 로그인 한 user 찾기
            String userEmail = jwtDecoder.decodeUsername(headerTokenExtractor.extract(jwtToken, httpServletRequest));
            User findUser = userRepository.findByEmail(userEmail).orElseThrow(
                    ()->new CustomException(ErrorCode.NOT_FOUND_USER)
            );
            // 현재 로그인 유저의 아티스트 이름과 request artist 가 같으면 같은 사람이므로 예외처리
            if(findUser.getArtist().equals(artist)) {
                throw new CustomException(ErrorCode.MOVED_TEMPORARILY);
            }
        }
        // 유저의 업로드[영상] List를 최신순으로 전체 가져오기
        userDataResponseDto.setData(feedRepository.findAllByPostTypeAndUser_ArtistOrderByIdDesc("video",artist));
        userDataResponseDto.setMessage("성공");
        userDataResponseDto.setSuccess(200);


        return ResponseEntity.ok().body(userDataResponseDto);
    }
}
