package com.tutti.backend.controller;


import com.tutti.backend.domain.ChatMessage;
import com.tutti.backend.dto.ChatMessageDto;
import com.tutti.backend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message, @Header("Authorization") String token) {
        log.info("요청 메서드 [Message] /chat/message");
        chatService.save(message, token);
    }

    // 이전 채팅 기록 가져오기
    @GetMapping("/chat/message/{roomId}")
    @ResponseBody
    public List<ChatMessage> getMessages(@PathVariable String roomId) {
        log.info("요청 메서드 [GET] /chat/message/{roomId}");
        return chatService.getMessages(roomId);
    }
}
