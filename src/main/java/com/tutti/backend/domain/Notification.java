package com.tutti.backend.domain;

import com.tutti.backend.dto.Notification.NotificationDetailsDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Not;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Notification extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(nullable=false)
    private User receiver;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(nullable=false)
    private LiveRoom liveRoom;


    @Column(nullable=false)
    private String content;

    @Column(nullable=false)
    private String url;

    @Column(nullable=false)
    private Boolean isRead;

    public Notification(User receiver, LiveRoom liveRoom, String content, String url, Boolean isRead) {
        this.receiver = receiver;
        this.liveRoom=liveRoom;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
    }

//    public Notification(NotificationDetailsDto notificationDetailsDto){
//        this.id=notificationDetailsDto.getId();
//        this.content= notificationDetailsDto.getContent();
//        this.url= notificationDetailsDto.getUrl();
//        this.isRead=notificationDetailsDto.getIsRead();
//        this.liveRoom=notificationDetailsDto
//    }
    public void read() {
        this.isRead = true;
    }

}
