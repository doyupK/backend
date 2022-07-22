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

import javax.annotation.Resource;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    @Resource(name = "stringTemplate")
    private ValueOperations<String,String > usernameCount;
    @Resource(name = "stringTemplate")
    private ValueOperations<String,String > sessionUsername;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        ValueOperations<String, String> usernameCount = canversationTemplate.opsForValue(); // 여기에는 username과 인원수가 들어간다. < K , V > -> < username, count >
//        ValueOperations<String, String> sessionUsername = canversationTemplate.opsForValue(); // 여기에는 sessionId와 username이  들어간다.  < K, V > -> < sessionId, username > 이렇게 맵핑
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message); // 주어진 메시지의 페이로드와 헤더에서 인스턴스를 만듭니다.
        String sessionId = accessor.getSessionId(); // 해당 유저의 세션 아이디

        if (SimpMessageType.SUBSCRIBE == accessor.getMessageType()) { // STOMP 명령을 반환하거나 아직 설정되지 않은 경우 null을 반환합니다.
            String fullDestination = accessor.getDestination(); // /chatroom/public{username} 이 들어가는 곳
            String username = fullDestination.substring(16); // {username}인 nugget만 추출
            sessionUsername.set(sessionId, username);
            // 인원수 한명 추가
            usernameCount.increment(username+2);
        } else if (SimpMessageType.DISCONNECT == accessor.getMessageType()) {
            String username = sessionUsername.get(sessionId); // 매핑해 둔 sessionId로 username 뽑아온 뒤 삭제
            // 인원수 한명 감소
            usernameCount.decrement(username+2);
        }
        return message;
    }
}
