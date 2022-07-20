package com.tutti.backend.domain;

import com.tutti.backend.dto.chatDto.Message;
import com.tutti.backend.dto.chatDto.Status;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class LiveRoomMessage extends Timestamped{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    String senderName; // 보낸사람

    @Column(nullable = false)
    String receiverName; // 받는사람

    @Column(nullable = false)
    String message; // 메시지 내용

    @Column(nullable = false)
    String date; // 시간? 날짜

    @Column(nullable = false)
    Status status; // ? 상태라는데

    @Column(nullable = false)
    String profileImage;

    @ManyToOne(fetch = FetchType.LAZY)
    private LiveRoom liveRoom;

    public LiveRoomMessage(Message message,LiveRoom liveRoom){
        this.senderName = message.getSenderName();
        this.receiverName = message.getReceiverName();
        this.message = message.getMessage();
        this.date = message.getDate();
        this.status = message.getStatus();
        this.profileImage = message.getProfileImage();
        this.liveRoom = liveRoom;
    }


}
