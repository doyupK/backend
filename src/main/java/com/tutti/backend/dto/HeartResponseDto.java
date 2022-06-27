package com.tutti.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HeartResponseDto {
    int success;
    String message;
    Boolean isHeart;
}
