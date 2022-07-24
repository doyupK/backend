package com.tutti.backend.security;


import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtExceptionFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response); // go to formLoginFilter();
        } catch (CustomException customException) {
            setErrorResponse(customException.getErrorCode(), response);
        }
    }
    public void setErrorResponse(ErrorCode errorCode, ServletResponse response) throws IOException {
        ((HttpServletResponse)response).setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json; charset=UTF-8");

        JwtExceptionResponse jwtExceptionResponse = new JwtExceptionResponse(errorCode.getErrorCode(), errorCode.getErrorMessage());
        response.getWriter().write(jwtExceptionResponse.convertToJson());
    }
}
