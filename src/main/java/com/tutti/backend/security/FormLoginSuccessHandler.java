package com.tutti.backend.security;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.tutti.backend.dto.user.ResponseDto;
import com.tutti.backend.dto.user.response.loginResponseDto;
import com.tutti.backend.security.jwt.JwtTokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 로그인 성공, 하면 토큰 발행
public class FormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_TYPE = "BEARER";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                                                    final Authentication authentication) throws IOException {
        final UserDetailsImpl userDetails = ((UserDetailsImpl) authentication.getPrincipal());
        // Token 생성
        final String token = JwtTokenUtils.generateJwtToken(userDetails);
        response.addHeader(AUTH_HEADER, TOKEN_TYPE + " " + token);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        loginResponseDto loginResponseDto = new loginResponseDto();




        loginResponseDto.setSuccess(200);
        loginResponseDto.setMessage("로그인 성공");
        loginResponseDto.setArtist(userDetails.getUser().getArtist());
        loginResponseDto.setProfileUrl(userDetails.getUser().getProfileUrl());

        String result = mapper.writeValueAsString(loginResponseDto);
        response.getWriter().write(result);
    }
}