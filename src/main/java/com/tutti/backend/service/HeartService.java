package com.tutti.backend.service;

import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.Heart;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.HeartResponseDto;
import com.tutti.backend.repository.FeedRepository;
import com.tutti.backend.repository.HeartRepository;
import com.tutti.backend.repository.UserRepository;
import com.tutti.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class HeartService {
    private final HeartRepository heartRepository;
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;


    @Transactional
    public Object click(Long feedId, UserDetailsImpl userDetails) {
        HeartResponseDto heartResponseDto = new HeartResponseDto();

        Long userId = userDetails.getUser().getId();

        Heart heart = heartRepository.findByUser_IdAndFeed_Id(userId, feedId);
        if (heart != null) {
            heart.update();
        } else {
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new NullPointerException("해당 유저를 찾을 수 없습니다.") // 커스텀 메시지
            );
            Feed feed = feedRepository.findById(feedId).orElseThrow(
                    () -> new NullPointerException("해당 게시글을 찾을 수 없습니다.") // 커스텀 메시지
            );
            heart = new Heart(user, feed);
            heartRepository.save(heart);
        }

        heartResponseDto.setSuccess(200);
        heartResponseDto.setMessage("성공!");
        heartResponseDto.setIsHeart(heart.getIsHeart());
        return heartResponseDto;
    }
}
