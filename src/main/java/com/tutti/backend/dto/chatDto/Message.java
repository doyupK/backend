package com.tutti.backend.dto.chatDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Message {
    String senderName; // 보낸사람
    String receiverName; // 받는사람
    String message; // 메시지 내용
    String date; // 시간? 날짜
    Status status; // ? 상태라는데
    String profileImage;
}