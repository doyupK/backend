package com.tutti.backend.config;

import com.tutti.backend.dto.chatDto.messageChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {


    @Autowired
    private StringRedisTemplate canversationTemplate;
    @Autowired
    private RedisTemplate<String, messageChannel> conversationTemplate;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // username과 인원수 저장 < K , V > -> < username, count >
        ValueOperations<String, String> usernameCount = canversationTemplate.opsForValue();
        // sessionId와 username 저장  < K, V > -> < sessionId, username > 이렇게 맵핑
        ValueOperations<String, String> sessionUsername = canversationTemplate.opsForValue();
        HashOperations<String, String, messageChannel> hashOperations = conversationTemplate.opsForHash();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId(); // 해당 유저의 세션 아이디

        if (SimpMessageType.SUBSCRIBE == accessor.getMessageType()) {
            String fullDestination = accessor.getDestination(); // /chatroom/public{username} 추출
            if(fullDestination.substring(0,2).contains("/c")){
                String username = fullDestination.substring(16); // {username}인 nugget만 추출
                sessionUsername.set(sessionId, username);
                // 인원수 한명 추가
                usernameCount.increment(username+2);
            }
        } else if (SimpMessageType.DISCONNECT == accessor.getMessageType()) {
            String username = sessionUsername.getAndDelete(sessionId); // sessionId로 username 가져온 뒤 삭제
            // 인원수 한명 감소
            usernameCount.decrement(username+2);
            if(Integer.parseInt(usernameCount.get(username+2)) < 1 ){
                usernameCount.getAndDelete(username+2);
            }
        }
        return message;
    }
}
