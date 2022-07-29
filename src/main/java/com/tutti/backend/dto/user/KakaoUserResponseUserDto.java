package com.tutti.backend.dto.user;

import com.tutti.backend.domain.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class KakaoUserResponseUserDto {
    private String first;

    private User user;
}
