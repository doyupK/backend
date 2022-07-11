package com.tutti.backend.chat.service;



import com.tutti.backend.chat.dto.ChatMessageDto;
import com.tutti.backend.chat.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisPublisher {
    private final RedisTemplate<String,Object> redisTemplate;


    public void publish (ChannelTopic topic , ChatMessageDto messageDto) {
        log.info("ChannelTopic : {}", topic.getTopic());
        log.info("ChatMessage : {}", messageDto.getType());
        redisTemplate.convertAndSend(topic.getTopic(),messageDto);
        log.info("발행 완료!");
    }

}
