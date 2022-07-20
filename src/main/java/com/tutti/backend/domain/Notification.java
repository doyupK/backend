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
    private Follow following;

    @Column(nullable=false)
    private String content;

    @Column(nullable=false)
    private String url;

    @Column(nullable=false)
    private Boolean isRead;

    public Notification(User receiver, Follow following, String content, String url, boolean isRead) {
        this.receiver = receiver;
        this.following = following;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
    }

    public Notification(NotificationDetailsDto notificationDetailsDto){
        this.id=notificationDetailsDto.getId();
        this.content= notificationDetailsDto.getContent();
        this.url= notificationDetailsDto.getUrl();
        this.isRead=notificationDetailsDto.getIsRead();
    }
    public void read() {
        this.isRead = true;
    }

}
