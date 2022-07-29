package com.tutti.backend.dto.user;

import com.tutti.backend.domain.User;
import lombok.Data;

@Data
public class KakaoUserResponseUserDto {
    private boolean first;

    private User user;
}
