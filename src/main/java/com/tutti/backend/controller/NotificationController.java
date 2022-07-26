package com.tutti.backend.controller;


import com.tutti.backend.service.NotificationService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe/{id}",produces = "text/event-stream")
    @Timed(value = "Notification subscribe", description = "Time to Create emitter")
    public SseEmitter subscribe(
            @PathVariable String id){

        return notificationService.subscribe(id);
    }
}
