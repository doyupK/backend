package com.tutti.backend.dto.Notification;

import com.tutti.backend.domain.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class NotificationCacheDto {

    private Long id;
    private String content;
    private String url;
    //    private Integer[] createdAt;
    private Boolean isRead;

    public NotificationCacheDto(Notification notification){
        this.id= notification.getId();
        this.content= notification.getContent();
        this.url=notification.getUrl();
        this.isRead=notification.getIsRead();
    }
}
