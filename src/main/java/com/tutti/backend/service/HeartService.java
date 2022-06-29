package com.tutti.backend.service;

import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.Heart;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.heart.HeartResponseDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
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
                    () -> new CustomException(ErrorCode.NOT_EXISTS_USERNAME) // 유저 정보 없을 때
            );
            Feed feed = feedRepository.findById(feedId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_FEED) // 피드 없을 때
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
