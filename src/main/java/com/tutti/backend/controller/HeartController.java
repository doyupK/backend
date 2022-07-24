package com.tutti.backend.controller;

import com.tutti.backend.security.UserDetailsImpl;
import com.tutti.backend.service.HeartService;
import io.micrometer.core.annotation.Timed;
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

    // 좋아요 클릭
    @PostMapping("/like/{feedId}")
    @Timed(value = "Like", description = "Time to like")
    public ResponseEntity<Object>  heartClick(@PathVariable("feedId") Long feedId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok().body(heartService.click(feedId, userDetails));
    }
}
