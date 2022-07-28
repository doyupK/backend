package com.tutti.backend.dto.Notification;

import com.tutti.backend.domain.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@Setter
@Getter
@NoArgsConstructor
public class NotificationDetailsDto {

    private Long id;
    private String content;
    private String url;
//    private Integer[] createdAt;
    private Boolean isRead;

    public NotificationDetailsDto(Notification notification){
        this.id= notification.getId();
        this.content= notification.getContent();
        this.url=notification.getUrl();
        this.isRead=notification.getIsRead();
    }

    public NotificationDetailsDto(Long id, String content, String url, Boolean isRead){
        this.id= id;
        this.content= content;
        this.url=url;
        this.isRead=isRead;
    }
}
