
package com.tutti.backend.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LiveChannelResponse {
    int success;
    String message;
    List<LiveChannelResponseDto> liveChannelList;
}