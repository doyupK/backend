package com.tutti.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutti.backend.dto.user.ResponseDto;
import org.apache.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
/* 로그인 실패 대응 로직 */
public class UserLoginFailHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        final ObjectMapper mapper = new ObjectMapper();
        ResponseDto loginResponseDto = new ResponseDto();

//        Exception 분별을 해야할 때
//        if (exception instanceof AuthenticationServiceException) {
//            request.setAttribute("loginFailMsg", "존재하지 않는 사용자입니다.");
//
//            loginResponseDto.setSuccess(false);
//            loginResponseDto.setMessage("존재하지 않는 사용자입니다.");
//
//        } else if (exception instanceof BadCredentialsException) {
//            request.setAttribute("loginFailMsg", "아이디 또는 비밀번호가 틀립니다.");
//
//            loginResponseDto.setSuccess(false);
//            loginResponseDto.setMessage("아이디 또는 비밀번호가 틀립니다.");
//        }
//

//      클론 프로젝트 로그인 실패는 1종류
        loginResponseDto.setSuccess(300);
        loginResponseDto.setMessage("로그인 실패");

        String result = mapper.writeValueAsString(loginResponseDto);
        response.getWriter().write(result);
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
    }
}