package com.tutti.backend.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

import static com.tutti.backend.security.jwt.JwtTokenUtils.*;


@Component
public class JwtDecoder {


    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public String decodeUsername(String token) {
        DecodedJWT decodedJWT = isValidToken(token);

        Date expiredDate = decodedJWT
                .getClaim(CLAIM_EXPIRED_DATE)
                .asDate();

        Date now = new Date(); // 해당 토큰의 날짜가 현재 검증하는 시간보다 이전인지 테스트 합니다.
        if (expiredDate.before(now)) { // NullPointerException -> null인 경우 -> 이 날짜가 지정된 날짜 이전인지 테스트합니다.
            throw new CustomException(ErrorCode.EXPIRED_JWT);
        }

        return decodedJWT
                .getClaim(CLAIM_USER_NAME)
                .asString();
    }

    private DecodedJWT isValidToken(String token) {
        DecodedJWT jwt;

        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT
                    .require(algorithm) // Algorithm.HMAC256(JWT_SECRET) 맞는지 검사
                    .build();

            jwt = verifier.verify(token);
            // JWTVerificationException -> 확인 단계 중 하나라도 실패할 경우 -> 주어진 토큰에 대해 검증을 수행합니다.
        } catch (Exception Exception) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILURE_JWT);
        }
        return jwt;
    }
}
