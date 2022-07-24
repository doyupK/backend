package com.tutti.backend.security;


import com.auth0.jwt.exceptions.TokenExpiredException;
import com.tutti.backend.exception.CustomException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

@Component
public class JwtExceptionFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response); // go to formLoginFilter();
        } catch (CustomException Exception) {
            setErrorResponse(Exception.getErrorCode().getHttpStatus(), Exception,
                            Exception.getErrorCode().getErrorCode(), response
                            );
        } catch (Exception Exception) {
            setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Exception,
                            "999", response
                            );
        }
    }
    // e를 통째로 던지고 싶지만 CustomException을 제외한 나머지 Exception들은
    // 형식이 message , cause , enableSuppression ,writableStackTrace 로 정해져있기 때문에
    // 받는 입장에서 어떤 데이터 형식으로 받아야 될지 모르겠네요.
    public void setErrorResponse(HttpStatus status, Throwable Exception,
                                 String statusNumber, ServletResponse response
                                ) throws IOException {
        // statusNumber = CustomErrorCode를 사용하기위해서 억지로 넣음.
        // status.value = int값인데 우리가쓰는 값(400_5)은 String이라서...
        ((HttpServletResponse)response).setStatus(status.value());
        response.setContentType("application/json; charset=UTF-8");

        JwtExceptionResponse jwtExceptionResponse = new JwtExceptionResponse(statusNumber, Exception.getMessage());
        response.getWriter().write(jwtExceptionResponse.convertToJson());
    }
}
