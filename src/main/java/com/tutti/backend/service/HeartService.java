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

    // 좋아요 클릭 시
    @Transactional
    public Object click(Long feedId, UserDetailsImpl userDetails) {
        HeartResponseDto heartResponseDto = new HeartResponseDto();

        Long userId = userDetails.getUser().getId();

        Heart heart = heartRepository.findByUser_IdAndFeed_Id(userId, feedId);
        /*
          좋아요를 처음 클릭하면 로그인 한 유저가 특정피드에 처음 좋아요를 클릭
             isHeart=true 를 default 값으로 Heart 생성
          두번째부터는 isHeart 반전
         */
        if (heart != null) {
            heartRepository.delete(heart);
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
