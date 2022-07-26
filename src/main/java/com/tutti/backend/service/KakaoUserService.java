package com.tutti.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tutti.backend.domain.User;
import com.tutti.backend.dto.user.KakaoUserRequestDto;
import com.tutti.backend.dto.user.KakaoUserResponseDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.repository.UserRepository;
import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.security.jwt.JwtTokenUtils;
import com.tutti.backend.security.provider.JWTAuthProvider;
import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoUserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public KakaoUserResponseDto kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = getAccessToken(code);
        KakaoUserRequestDto kakaoUserRequestDto = getKakaoUserInfo(accessToken);

        User kakaoUser = registerKakaoUserIfNeeded(kakaoUserRequestDto);
        return forceLoginUser(kakaoUser, response);
    }

    private KakaoUserRequestDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
        System.out.println(3);
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();
        System.out.println(4);
        System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoUserRequestDto(id, nickname,email);
    }

    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "346b2f15b0bcf829529a506449139680");


        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private User registerKakaoUserIfNeeded(KakaoUserRequestDto kakaoUserRequestDto) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserRequestDto.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);
        if (kakaoUser == null) {
            // 회원가입
            // username: kakao nickname
            String nickname = kakaoUserRequestDto.getNickname();

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            // email: kakao email
            String email = kakaoUserRequestDto.getEmail();
            if(email.equals("")){
                throw new CustomException(ErrorCode.NOT_EXISTS_KAKAOEMAIL);
            }
            // role: 일반 사용자
            kakaoUser = new User(email, encodedPassword, nickname, kakaoId);
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

    private KakaoUserResponseDto forceLoginUser(User kakaoUser, HttpServletResponse response) {
        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT토큰 헤더에 생성
        String token = JwtTokenUtils.generateJwtToken(userDetails);
        String username = kakaoUser.getEmail();
        response.addHeader("Authorization", "Bearer " + token);

        if (kakaoUser.getEmail().equals("")) {
            boolean result = false;
            return KakaoUserResponseDto.builder()
                    .JWtToken(token)
                    .username(username)
                    .result(result)
                    .build();

        } else {
            boolean result = true;
            return KakaoUserResponseDto.builder()
                    .JWtToken(token)
                    .username(username)
                    .result(result)
                    .build();
        }
    }
}
