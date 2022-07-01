package com.tutti.backend.chat.controller;


import com.tutti.backend.chat.model.Conversation;
import com.tutti.backend.chat.model.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Objects;

@RestController
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final RedisTemplate<String, Conversation> conversationTemplate;

    @Autowired
    public MessageController(SimpMessagingTemplate simpMessagingTemplate, RedisTemplate<String, Conversation> conversationTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.conversationTemplate = conversationTemplate;
    }


    @MessageMapping("/send")
    public void SendToMessage(MessageDto messageDto) {
        logger.info("{}", messageDto);
        HashOperations<String, String, Conversation> ho = conversationTemplate.opsForHash();
        if (ho.hasKey(messageDto.getAuthor(), messageDto.getTo())) { // 상대방과 대화 데이터가 있을때
            Conversation conversation = ho.get(messageDto.getAuthor(), messageDto.getTo());
            Objects.requireNonNull(conversation).getMessageList().add(messageDto);
            ho.put(messageDto.getAuthor(), messageDto.getTo(), conversation);
        } else { // 상대방 데이터가 없을때
            Conversation newConversation = new Conversation(messageDto.getTo(), new ArrayList<>());
            newConversation.getMessageList().add(messageDto);
            ho.put(messageDto.getAuthor(), messageDto.getTo(), newConversation);
        }
        if (ho.hasKey(messageDto.getTo(), messageDto.getAuthor())) { // 상대방에게 대화 데이터가 있을때
            Conversation conversation = ho.get(messageDto.getTo(), messageDto.getAuthor());
            Objects.requireNonNull(conversation).getMessageList().add(messageDto);
            ho.put(messageDto.getTo(), messageDto.getAuthor(), conversation);
        } else { // 상대방에게 대화 데이터가 없을때
            Conversation newConversation = new Conversation(messageDto.getAuthor(), new ArrayList<>());
            newConversation.getMessageList().add(messageDto);
            ho.put(messageDto.getTo(), messageDto.getAuthor(), newConversation);
        }
        simpMessagingTemplate.convertAndSend("/topic/" + messageDto.getTo(), messageDto);
    }
}