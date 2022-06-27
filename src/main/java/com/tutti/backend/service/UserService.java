package com.tutti.backend.service;


import com.tutti.backend.domain.ConfirmationToken;
import com.tutti.backend.domain.User;
import com.tutti.backend.domain.UserConfirmEnum;
import com.tutti.backend.dto.user.*;
import com.tutti.backend.dto.user.request.ArtistRequestDto;
import com.tutti.backend.dto.user.request.EmailRequestDto;
import com.tutti.backend.repository.UserRepository;
import com.tutti.backend.security.UserDetailsImpl;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.Optional;


@Service
@Transactional
public class UserService {

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;


    @Autowired
    public UserService(S3Service s3Service, UserRepository userRepository, PasswordEncoder passwordEncoder, ConfirmationTokenService confirmationTokenService) {
        this.s3Service = s3Service;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
    }

    @Transactional
    public ResponseEntity<?> registerUser(SignupRequestDto signupRequestDto, MultipartFile file) {
        ResponseDto signupResponseDto = new ResponseDto();


//      ID 중복 체크
        if (userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("중복된 아이디가 포함되어 있습니다.");
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
        if(!user.isPresent()){
            throw new NullPointerException("사용자 정보가 없습니다.");
        }

        signupResponseDto.setSuccess(200);
        signupResponseDto.setMessage("사용할 수 있는 이메일입니다.");
        return ResponseEntity.ok().body(signupResponseDto);
    }

    public ResponseEntity<?> getUserArtistCheck(ArtistRequestDto artistRequestDto) {
        ResponseDto signupResponseDto = new ResponseDto();
        Optional<User> user = userRepository.findByArtist(artistRequestDto.getArtist());
        if(!user.isPresent()){
            throw new NullPointerException("사용자 정보가 없습니다.");
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
            throw new IllegalArgumentException("잘못된 토큰값");
        }

//      User Confirm 정보 'OK' 로 변경
        findUserInfo.get().setUserConfirmEnum(UserConfirmEnum.OK_CONFIRM);
    }

    public ResponseEntity<?> getUserDetail(UserDetailsImpl userDetails) {
        Optional<User> user = userRepository.findByEmail(userDetails.getUser().getEmail());
        if(!user.isPresent()){
            throw new NullPointerException("사용자 정보가 없습니다.");
        }

        return null;
    }



}
