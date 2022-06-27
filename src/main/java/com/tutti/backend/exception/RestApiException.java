package com.tutti.backend.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@NoArgsConstructor
@Getter
@Setter
public class RestApiException { // http 본문에 담아서 보내줄 형태
    private String errorMessage;
    private HttpStatus httpStatus;

    private String code;

    public RestApiException(ErrorCode code){
        this.httpStatus = code.getHttpStatus();
        this.code = getCode();
        this.errorMessage = getErrorMessage();
    }
    public static RestApiException of(ErrorCode code) {
        return new RestApiException(code);
    }
}

