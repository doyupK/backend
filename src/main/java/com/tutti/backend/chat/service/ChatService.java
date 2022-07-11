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
        messageDto.setSender(user.getArtist());
/*        messageDto.setProfileUrl(user.getProfileUrl());*/
        messageDto.setEnterUserCnt(enterUserCnt);
//        Date date = new Date();
//        chatMessage.setCreateAt(date); // 시간세팅

        if (ChatMessage.MessageType.ENTER.equals(messageDto.getType())) {
            chatRoomRepository.enterChatRoom(messageDto.getRoomId());

            messageDto.setMessage("[알림] " + messageDto.getSender() + "님이 입장하셨습니다.");
/*            messageDto.setProfileUrl(null);*/

        } else if (ChatMessage.MessageType.QUIT.equals(messageDto.getType())) {

            messageDto.setMessage("[알림] " + messageDto.getSender() + "님이 나가셨습니다.");
/*            messageDto.setProfileUrl(null);*/
        }

        log.info("ENTER : {}", messageDto.getMessage());

        chatMessageRepository.save(messageDto); // 캐시에 저장 했다.
        // Websocket 에 발행된 메시지를 redis 로 발행한다(publish)
        redisPublisher.publish(ChatRoomRepository.getTopic(messageDto.getRoomId()), messageDto);
    }


    //redis에 저장되어있는 message 들 출력
    public List<ChatMessageDto> getMessages(String roomId) {
        log.info("getMessages roomId : {}", roomId);
        return chatMessageRepository.findAllMessage(roomId);
    }

}

