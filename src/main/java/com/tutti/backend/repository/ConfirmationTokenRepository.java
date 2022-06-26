package com.tutti.backend.repository;



import com.tutti.backend.domain.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;


public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,String> {
    Optional<ConfirmationToken> findByIdAndExpirationDateAfterAndExpired(String confirmationTokenId, LocalDateTime now, boolean expired);
}