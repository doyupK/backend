package com.tutti.backend.service;

import com.tutti.backend.chat.dto.LiveChannelResponse;
import com.tutti.backend.chat.dto.LiveChannelResponseDto;
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

import java.util.ArrayList;
import java.util.List;

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

    public Object readPost(User user) {
        LiveChannelResponse liveChannelResponse = new LiveChannelResponse();
        List<LiveChannelResponseDto> liveChannelResponseDtoList = new ArrayList<>();

        List<VideoChatPost> postList = videoChatPostRepository.findAll();
        for (VideoChatPost postDto : postList) {
            liveChannelResponseDtoList.add(new LiveChannelResponseDto(
                    postDto.getArtist(),
                    postDto.getProfileImageUrl(),
                    postDto.getTitle(),
                    postDto.getThumbNailImageUrl()));
        }

        liveChannelResponse.setSuccess(200);
        liveChannelResponse.setMessage("성공");
        liveChannelResponse.setLiveChannelList(liveChannelResponseDtoList);
        return liveChannelResponse;
    }
}
