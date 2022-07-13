package com.tutti.backend.chat.model;


import com.tutti.backend.chat.dto.ChatMessageDto;
import lombok.*;

import javax.persistence.*;


@Setter
@Builder
@AllArgsConstructor
@Getter
@Entity
@NoArgsConstructor
public class ChatMessage {

    // 메시지 타입 : 입장, 채팅, 타입을 같이 주셔야합니다.
    public enum MessageType {
        ENTER, TALK, QUIT
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private String roomId; // 방번호 (postId)

    @Enumerated(EnumType.STRING)
    @Column
    private MessageType type; // 메시지 타입

    @Column
    private String sender; // nickname
    @Column
    private String message; // 메시지
    @Column
    private Long enterUserCnt;

    @JoinColumn(name = "CHAT_ROOM_ID")
    @ManyToOne
    private ChatRoom chatRoom;


    //    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonFormat(pattern = "yyyy-mm-dd HH:mm")
//    private Date createAt;

    public ChatMessage(ChatMessageDto chatMessageDto) {
        this.type = chatMessageDto.getType();
        this.roomId = chatMessageDto.getRoomId();
        this.message = chatMessageDto.getMessage();
        this.sender = chatMessageDto.getSender();
        this.enterUserCnt = chatMessageDto.getEnterUserCnt();
    }
}
