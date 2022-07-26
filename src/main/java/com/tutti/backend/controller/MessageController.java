package com.tutti.backend.controller;


import com.tutti.backend.dto.chatDto.Status;
import com.tutti.backend.dto.chatDto.messageChannel;
import com.tutti.backend.dto.chatDto.Message;
import com.tutti.backend.security.UserDetailsImpl;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.handler.MessagingAdviceBean;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@Controller
@Slf4j
public class MessageController {
    HashMap<String, messageChannel> hashMap = new HashMap<>();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private RedisTemplate<String, messageChannel> conversationTemplate;

    @Autowired
    private StringRedisTemplate canversationTemplate;


    @MessageMapping({"/message","/message/{username}"}) // /app/message 이리로 보내면  (공개대화방 )
//    @SendTo("/chatroom/public") // 처리를 마친 후 이리로 메세지를 보내겠다. 이리로 다 보내라?
    @Timed(value = "Message", description = "Time to Send and Save Message")
    public Message receiveMessage(@Payload Message message, @DestinationVariable String username){
        HashOperations<String, String, messageChannel> ho = conversationTemplate.opsForHash();
        ValueOperations<String, String> usernameCount = canversationTemplate.opsForValue();
        message.setCount(String.valueOf(usernameCount.get(username+2)));

        if (ho.hasKey(username, username)) { // 데이터가 있을때
            // 메시지 타입이 JOIN이면 메시지 송신 X
            if(message.getStatus() == Status.JOIN){ return message; }

            messageChannel messageChannel = ho.get(username, username);
            messageChannel.getMessageList().add(message);
            ho.put(username, username, messageChannel);
        }
        // 데이터가 없을때 + 방송을 종료 했을때 Message Send 막기
        else { return message; }
        simpMessagingTemplate.convertAndSend("/chatroom/public"+username,message);
        return message;
    }
    @SubscribeMapping("/subscribe/{username}") // Subscripbe 할 때 메시지 이력 읽어오기
    public List<Message> subscribeMessage(@DestinationVariable String username){
        HashOperations<String, String, messageChannel> ho = conversationTemplate.opsForHash();
        messageChannel messageChannel = ho.get(username, username);
        return messageChannel.getMessageList();
    }

}