package com.tutti.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    @Autowired
    private StringRedisTemplate canversationTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // username과 인원수 저장 < K , V > -> < username, count >
        ValueOperations<String, String> usernameCount = canversationTemplate.opsForValue();
        // sessionId와 username 저장  < K, V > -> < sessionId, username > 이렇게 맵핑
        ValueOperations<String, String> sessionUsername = canversationTemplate.opsForValue();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId(); // 해당 유저의 세션 아이디

        if (SimpMessageType.SUBSCRIBE == accessor.getMessageType()) {
            String fullDestination = accessor.getDestination(); // /chatroom/public{username} 추출
            String username = fullDestination.substring(16); // {username}인 nugget만 추출
            sessionUsername.set(sessionId, username);
            // 인원수 한명 추가
            usernameCount.increment(username+2);
        } else if (SimpMessageType.DISCONNECT == accessor.getMessageType()) {
            String username = sessionUsername.getAndDelete(sessionId); // sessionId로 username 가져온 뒤 삭제
            // 인원수 한명 감소
            usernameCount.decrement(username+2);
        }
        return message;
    }
}
