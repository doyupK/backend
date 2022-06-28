package com.tutti.backend.dto.google;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleUserResponseDto {
    private String token;
    private String email;
    private String nickname;
    private String profileImage;

}
