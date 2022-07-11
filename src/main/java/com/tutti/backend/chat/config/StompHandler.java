package com.tutti.backend.chat.config;


import com.tutti.backend.chat.repository.ChatMessageRepository;
import com.tutti.backend.chat.service.ChatRoomService;
import com.tutti.backend.security.jwt.JwtDecoder;
import com.tutti.backend.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {


    private JwtDecoder jwtDecoder;

    private final ChatRoomService chatRoomService;
    private final ChatMessageRepository chatMessageRepository;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("30, simpDestination : {}", message.getHeaders().get("simpDestination"));
        log.info("31, sessionId : {}", message.getHeaders().get("simpSessionId"));
        String sessionId = (String) message.getHeaders().get("simpSessionId");
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
//            sessionId = (String) message.getHeaders().get("simpSessionId");
            log.info("CONNECT : {}", sessionId);
            jwtDecoder.isValidToken(accessor.getFirstNativeHeader("token"));
        }else if(StompCommand.SUBSCRIBE == accessor.getCommand()){
            log.info("SUBSCRIBE : {}", sessionId);
//            sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomService.getRoomId((String) Optional.ofNullable(message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            log.info("roomId : {}", roomId);
            chatMessageRepository.setUserEnterInfo(roomId, sessionId);
            chatMessageRepository.plusUserCnt(roomId);

        }else if (StompCommand.DISCONNECT == accessor.getCommand()) {

//            sessionId = (String) message.getHeaders().get("simpSessionId");
            log.info("DISCONNECT : {}", sessionId);
            String roomId = chatMessageRepository.getRoomId(sessionId);
            log.info("roomId: {}", roomId);

            chatMessageRepository.minusUserCnt(sessionId, roomId);

            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatMessageRepository.removeUserEnterInfo(sessionId);
        }
        return message;
    }
}

