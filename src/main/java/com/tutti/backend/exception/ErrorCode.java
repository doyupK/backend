package com.tutti.backend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ErrorCode {
    // 3xx Redirection
    MOVED_TEMPORARILY(HttpStatus.FOUND, "302_01","마이페이지로 이동해주세요"),
    // 400 Bad Request
    WRONG_USER(HttpStatus.BAD_REQUEST, "400", "올바른 유저가 아닙니다."),
    NULL_TITLE(HttpStatus.BAD_REQUEST, "400_1","제목을 입력해 주세요"),
    NULL_ADDRESS(HttpStatus.BAD_REQUEST, "400_2","장소를 입력해 주세요"),
    NULL_CONTENT(HttpStatus.BAD_REQUEST, "400_3","내용을 입력해 주세요"),
    TEMPORARY_SERVER_ERROR(HttpStatus.BAD_REQUEST, "400_4", "잘못된 요청입니다."),
    EXIST_EMAIL(HttpStatus.BAD_REQUEST, "400_5", "이미 존재하는 이메일입니다."),
    EXIST_ARTIST(HttpStatus.BAD_REQUEST, "400_6", "이미 존재하는 아티스트명 입니다."),
    WRONG_FORMAT_EMAIL(HttpStatus.BAD_REQUEST, "400_7", "올바른 이메일 형식을 입력해주세요."),
    WRONG_FORMAT_PASSWORD(HttpStatus.BAD_REQUEST, "400_8", "비밀번호는 영어와 숫자로 4~12 자리로 입력해주세요."),
    PASSWORD_NOT_CONFIRM(HttpStatus.BAD_REQUEST, "400_9", "비밀번호와 비밀번호 확인이 다릅니다."),
    NOT_EXISTS_USERNAME(HttpStatus.BAD_REQUEST, "400_10", "존재하지 않는 아이디입니다."),
    NOT_EXISTS_PASSWORD(HttpStatus.BAD_REQUEST, "400_11", "존재하지 않는 비밀번호입니다."),
    NOT_EXISTS_KAKAOEMAIL(HttpStatus.BAD_REQUEST, "400_12", "카카오 이메일이 존재하지 않습니다."),
    // 404 Not Found
    NOT_FOUND_FEED(HttpStatus.NOT_FOUND, "404_1", "해당 피드가 존재하지 않습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "404_2", "해당 댓글이 존재하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404_3", "로그인을 해주세요."),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "404_4","토큰을 찾지 못했습니다."),
    NOT_FOUND_VIDEOCHATPOST(HttpStatus.NOT_FOUND, "404_3", "채널을 찾지 못하였습니다."),
    // 405 file
    WRONG_FILE_TYPE(HttpStatus.BAD_REQUEST,"405_1", "잘못된 형식의 파일입니다."),
    FAIL_FILE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR,"405_2", "파일 업로드에 실패하였습니다."),



    ;




    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    /*ErrorCode(HttpStatus httpStatus, String errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }*/
}
