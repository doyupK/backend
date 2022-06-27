package com.tutti.backend.controller;

import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HeartController {
    private final HeartService heartService;

    @PostMapping("/like/{feedId}")
    public ResponseEntity<Object>  heartClick(@PathVariable("feedId") Long feedId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok().body(heartService.click(feedId, userDetails));
    }
}
