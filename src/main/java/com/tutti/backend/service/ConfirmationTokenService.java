package com.tutti.backend.service;


import com.tutti.backend.domain.ConfirmationToken;
import com.tutti.backend.repository.ConfirmationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailSenderService emailSenderService;


    public void createEmailConfirmationToken(String receiverEmail) {


//      인증 Token 정보 DB 저장
        ConfirmationToken emailConfirmationToken = ConfirmationToken.createEmailConfirmationToken(receiverEmail);
        confirmationTokenRepository.save(emailConfirmationToken);

//      Mail Message 생성
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiverEmail);
        mailMessage.setSubject("회원가입 이메일 인증");
        mailMessage.setText("인증 링크 : "+"http://localhost:8080/confirm-email?token=" + emailConfirmationToken.getId());
        emailSenderService.sendEmail(mailMessage);
    }


    public ConfirmationToken findByIdAndExpirationDateAfterAndExpired(String confirmationTokenId) {
        Optional<ConfirmationToken> confirmationToken = confirmationTokenRepository.findByIdAndExpirationDateAfterAndExpired(confirmationTokenId, LocalDateTime.now(), false);
        return confirmationToken.orElseThrow(() -> new BadCredentialsException("Token Not Found"));
    }
}
