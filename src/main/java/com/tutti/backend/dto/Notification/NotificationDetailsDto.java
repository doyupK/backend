package com.tutti.backend.dto.Notification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class NotificationDetailsDto {

    private Long id;
    private String content;
    private String url;
    private Integer[] createdAt;
    private Boolean isRead;


}
