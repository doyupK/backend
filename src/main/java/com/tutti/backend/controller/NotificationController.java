package com.tutti.backend.controller;

import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.NotificationService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.experimental.StandardException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Controller
@RequiredArgsConstructor
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe/{id}",produces = "text/event-stream")
    @Timed(value = "Notification subscribe", description = "Time to Create emitter")
    public SseEmitter subscribe(
            @PathVariable String id,
//            @AuthenticationPrincipal UserDetailsImpl userDetails,
                                @RequestHeader(value="lastEventId",required = false,defaultValue = "") String lastEventId
    ){

        SseEmitter sseEmitter=notificationService.subscribe(id,lastEventId);
        log.info("5");

        return sseEmitter;
    }
}
