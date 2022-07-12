package com.tutti.backend.dto.chatDto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class messageChannel {
    String roomId; // 채널 고유값
    String owner; // 만든사람
    String channelName; // 채널이름
    List<Message> messageList;
}
