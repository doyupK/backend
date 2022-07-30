package com.tutti.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tutti.backend.domain.User;
import com.tutti.backend.dto.user.KakaoUserRequestDto;
import com.tutti.backend.dto.user.KakaoUserResponseDto;
import com.tutti.backend.dto.user.KakaoUserResponseUserDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.repository.UserRepository;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.security.jwt.JwtTokenUtils;
import com.tutti.backend.security.provider.JWTAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public KakaoUserResponseDto kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
//        log.info("2");
        String accessToken = getAccessToken(code);
//        log.info("3");
        KakaoUserRequestDto kakaoUserRequestDto = getKakaoUserInfo(accessToken);
        KakaoUserResponseUserDto kakaoUserResponseUserDto = registerKakaoUserIfNeeded(kakaoUserRequestDto);
        return forceLoginUser(kakaoUserResponseUserDto.getUser(), response,kakaoUserResponseUserDto);
    }

    private KakaoUserRequestDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
//        log.info("4");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        log.info("5");
        // HTTP 요청 보내기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
//        log.info("6");
        System.out.println(3);
        String responseBody = response.getBody();
//        log.info("6-1");
        ObjectMapper objectMapper = new ObjectMapper();
//        log.info("6-2");
        JsonNode jsonNode = objectMapper.readTree(responseBody);
//        log.info("6-3");
        Long id = jsonNode.get("id").asLong();
//        log.info("6-4");
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
/*        String email = jsonNode.get("kakao_account")
                .get("email").asText();*/
//        log.info("6-5");
        log.info(jsonNode.toString());
        String profileUrl = jsonNode.get("properties")
                .get("profile_image").asText();
//        log.info("7");
/*        System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);*/
        return new KakaoUserRequestDto(id, nickname,profileUrl);
    }

    private String getAccessToken(String code) throws JsonProcessingException {
//        log.info("8");
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        log.info("9");
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "346b2f15b0bcf829529a506449139680");

//        log.info("10");
        body.add("redirect_uri", "https://tuttimusic.shop/oauth/callback/kakao");
        body.add("code", code);

        // HTTP 요청 보내기
//        log.info("11");
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
//        log.info("12");

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private KakaoUserResponseUserDto registerKakaoUserIfNeeded(KakaoUserRequestDto kakaoUserRequestDto) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        KakaoUserResponseUserDto kakaoUserResponseUserDto = new KakaoUserResponseUserDto();
        kakaoUserResponseUserDto.setFirst("alreadyDone");
//        log.info("13");
        Long kakaoId = kakaoUserRequestDto.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);
        kakaoUserResponseUserDto.setUser(kakaoUser);
        if (kakaoUser == null) {
//            log.info("14");
            // 회원가입
            // username: kakao nickname

            String ranNum = UUID.randomUUID().toString().substring(0,3);

            String artist = kakaoUserRequestDto.getNickname()+ranNum;

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
            String profileUrl = kakaoUserRequestDto.getProfileUrl();
            // email: kakao email
            String email = UUID.randomUUID().toString();
            String profileText = "안녕하세요. 처음 뵙겠습니다. 잘부탁드려요!";
            if(email.equals("")){
                throw new CustomException(ErrorCode.NOT_EXISTS_KAKAOEMAIL);
            }
//            log.info("15");
            // role: 일반 사용자
            kakaoUser = new User(email, encodedPassword, artist,profileUrl, kakaoId,profileText);
            userRepository.save(kakaoUser);
            kakaoUserResponseUserDto.setUser(kakaoUser);
            kakaoUserResponseUserDto.setFirst("firstTime");
        }

//        log.info("16");
        return kakaoUserResponseUserDto;
    }

    private KakaoUserResponseDto forceLoginUser(User kakaoUser, HttpServletResponse response,KakaoUserResponseUserDto kakaoUserResponseUserDto) {
//        log.info("17");
        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
//        log.info("18");
        // JWT토큰 헤더에 생성
        String token ="Bearer " + JwtTokenUtils.generateJwtToken(userDetails);
        String artist = kakaoUser.getArtist();
        String profileImage = kakaoUser.getProfileUrl();
        response.addHeader("Authorization",  token);
        String RegisterCheck = kakaoUserResponseUserDto.getFirst();
//        log.info("19");
        // 토큰의 만료시간 전달
        Date date = new Date(System.currentTimeMillis() + 86400*1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime2 = dateFormat.format(date);
//        log.info("20");
        /*if (kakaoUser.getEmail().equals("")) {
            boolean result = false;
            return KakaoUserResponseDto.builder()
                    .JWtToken(token)
                    .artist(artist)
                    .result(result)
                    .build();

        } else {*//*
            boolean result = true;*/
            return KakaoUserResponseDto.builder()
                    .JWtToken(token)
                    .artist(artist)
/*                    .result(result)*/
                    .profileUrl(profileImage)
                    .nowTime2(nowTime2)
                    .RegisterCheck(RegisterCheck)
                    .build();
        //}
    }
}
