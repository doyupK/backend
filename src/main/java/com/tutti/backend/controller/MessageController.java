package com.tutti.backend.controller;


import com.tutti.backend.dto.chatDto.messageChannel;
import com.tutti.backend.dto.chatDto.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@Controller
public class MessageController {
    HashMap<String, messageChannel> hashMap = new HashMap<>();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private RedisTemplate<String, messageChannel> conversationTemplate;


    @MessageMapping({"/message","/message/{username}"}) // /app/message 이리로 보내면  (공개대화방 )
//    @SendTo("/chatroom/public") // 처리를 마친 후 이리로 메세지를 보내겠다. 이리로 다 보내라?
    public Message receiveMessage(@Payload Message message, @DestinationVariable String username){

        HashOperations<String, String, messageChannel> ho = conversationTemplate.opsForHash();

        if (ho.hasKey(username, username)) { // 데이터가 있을때
            messageChannel messageChannel = ho.get(username, username);
            messageChannel.getMessageList().add(message);
            ho.put(username, username, messageChannel);
        }

        else { // 데이터가 없을때
            List<Message> emptyMessageList = new ArrayList<>();
            messageChannel newMessageChannel =
                    new messageChannel(UUID.randomUUID().toString(),username,username,emptyMessageList);
            newMessageChannel.getMessageList().add(message);
            ho.put(username, username, newMessageChannel);

        }
        simpMessagingTemplate.convertAndSend("/chatroom/public"+username,message);
        return message;
    }

//    @MessageMapping("/private-message") // (개인 메시지 )
//    public Message recMessage(@Payload Message message){
//        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message); // /user/david/private
//        System.out.println(message.toString());
//        return message;
//    }

//    @ResponseBody
//    @PostMapping("/api/channel") // 나중에 통일 -> 만들기 눌렀을 때
//    public void createChannel(@RequestBody String username) {
//        Channel channel = new Channel(username);
//        hashMap.put(channel.getRoomId(),channel);
//    }

//    @ResponseBody
//    @GetMapping("/api/channel")
//    public List<Channel> readChannel() {
//        hashMap
//    }

}