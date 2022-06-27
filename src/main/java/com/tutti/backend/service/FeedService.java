package com.tutti.backend.service;

import com.sun.org.apache.xpath.internal.operations.Mult;
import com.tutti.backend.domain.Feed;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.Feed.FeedRequestDto;
import com.tutti.backend.dto.Feed.FeedUpdateRequestDto;
import com.tutti.backend.dto.user.FileRequestDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.repository.FeedRepository;
import com.tutti.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FeedService {
    
    private final FeedRepository feedRepository;

    private final S3Service service;

    @Transactional
    public void createFeed(FeedRequestDto feedRequestDto, MultipartFile albumImage, MultipartFile song, User user) {
        FileRequestDto albumDto = service.upload(albumImage);
        FileRequestDto songDto = service.upload(song);
        String albumImageUrl = albumDto.getImageUrl();
        String songUrl = songDto.getImageUrl();

        Feed feed = new Feed(feedRequestDto.getTitle(),
                feedRequestDto.getDescription(),
                albumImageUrl,
                songUrl,
                feedRequestDto.getGenre(),
                feedRequestDto.getPostType(),
                feedRequestDto.getColor(),
                user
                );

        feedRepository.save(feed);
    }
//    @Transactional
//    public void updateFeed(Long feedId, FeedUpdateRequestDto feedUpdateRequestDto) {
//        Feed fedd = feedRepository.findById(feedId).orElseThrow(new CustomException(ErrorCode.NOT_FOUND_FEED));
//
//
//    }
}
