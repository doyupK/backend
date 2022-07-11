package com.tutti.backend.service;

import com.tutti.backend.controller.PostRequestDto;
import com.tutti.backend.domain.User;
import com.tutti.backend.domain.VideoChatPost;
import com.tutti.backend.dto.user.FileRequestDto;
import com.tutti.backend.repository.UserRepository;
import com.tutti.backend.repository.VideoChatPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
@Service
@Transactional
@RequiredArgsConstructor
public class VideoChatPostService {

    private final S3Service service;

    private final UserRepository userRepository;

    private final VideoChatPostRepository videoChatPostRepository;

    public void createPost(User user, PostRequestDto requestDto, MultipartFile file) {
        FileRequestDto thumbNailImageDto = service.upload(file);

        String thumbNailImageDtoImageUrl = thumbNailImageDto.getImageUrl();

        VideoChatPost videoChatPost = new VideoChatPost(
                requestDto,
                user,
                thumbNailImageDtoImageUrl
        );
        videoChatPostRepository.save(videoChatPost);

    }
}
