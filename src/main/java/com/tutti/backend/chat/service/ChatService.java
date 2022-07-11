package com.tutti.backend.chat.service;



import com.tutti.backend.chat.dto.ChatMessageDto;
import com.tutti.backend.chat.model.ChatMessage;
import com.tutti.backend.chat.repository.ChatMessageRepository;
import com.tutti.backend.chat.repository.ChatRoomRepository;
import com.tutti.backend.domain.User;
import com.tutti.backend.repository.UserRepository;
import com.tutti.backend.security.jwt.JwtDecoder;
import com.tutti.backend.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    public void save(ChatMessageDto messageDto, String token) {
        log.info("save Message : {}", messageDto.getMessage());

        Long enterUserCnt = chatMessageRepository.getUserCnt(messageDto.getRoomId());

        String email = jwtDecoder.decodeUsername(token); // 토큰에서 유저 아이디 가져오기

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NullPointerException("존재하지 않는 사용자 입니다!")
        );
        ChatMessage chatMessage = new ChatMessage(messageDto);
        chatMessage.setSender(user.getArtist());
/*        chatMessage.setProfileUrl(user.getProfileUrl());*/
        chatMessage.setEnterUserCnt(enterUserCnt);
//        Date date = new Date();
//        chatMessage.setCreateAt(date); // 시간세팅

        log.info("type : {}", chatMessage.getType());

        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatRoomRepository.enterChatRoom(chatMessage.getRoomId());

            chatMessage.setMessage("[알림] " + chatMessage.getSender() + "님이 입장하셨습니다.");
            chatMessage.setProfileUrl(null);

        } else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {

            chatMessage.setMessage("[알림] " + chatMessage.getSender() + "님이 나가셨습니다.");
/*            chatMessage.setProfileUrl(null);*/
        }

        log.info("ENTER : {}", chatMessage.getMessage());

        chatMessageRepository.save(chatMessage); // 캐시에 저장 했다.
        // Websocket 에 발행된 메시지를 redis 로 발행한다(publish)
        redisPublisher.publish(ChatRoomRepository.getTopic(chatMessage.getRoomId()), chatMessage);
    }


    //redis에 저장되어있는 message 들 출력
    public List<ChatMessageDto> getMessages(String roomId) {
        log.info("getMessages roomId : {}", roomId);
        return chatMessageRepository.findAllMessage(roomId);
    }

}

