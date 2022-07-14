package com.tutti.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // 메시지 핸들러 ( ChatController로  라우팅 됨 )
        registry.enableSimpleBroker("/chatroom", "/user"); // 내장 브로커 사용
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/wss").setAllowedOriginPatterns("*")
                .setAllowedOrigins("https://tuttimusic.shop","http://localhost:3000").withSockJS(); // SockJS = 웹소켓을지원하지 않는브라우저환경에는 비슷한경험제공
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(stompHandler); // 이 메시지 채널에 대해 주어진 인터셉터를 구성하여 채널의 현재 인터셉터 목록에 추가합니다.
    }
}