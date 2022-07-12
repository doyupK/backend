package com.tutti.backend.service;


import com.tutti.backend.chat.dto.ChatMessageDto;
import com.tutti.backend.chat.model.ChatMessage;
import com.tutti.backend.chat.model.ChatRoom;
import com.tutti.backend.chat.repository.ChatRoomRepository;
import com.tutti.backend.chat.service.RedisSubscriber;
import com.tutti.backend.domain.Channel;
import com.tutti.backend.dto.PostRequestDto;

import com.tutti.backend.chat.dto.LiveChannelResponse;
import com.tutti.backend.chat.dto.LiveChannelResponseDto;
import com.tutti.backend.domain.User;
import com.tutti.backend.dto.user.FileRequestDto;
import com.tutti.backend.exception.CustomException;
import com.tutti.backend.exception.ErrorCode;
import com.tutti.backend.repository.UserRepository;
import com.tutti.backend.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChannelService {

    private final S3Service service;

    private final UserRepository userRepository;

    private final ChannelRepository channelRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final RedisSubscriber redisSubscriber;
    public void createPost(User user, PostRequestDto requestDto, MultipartFile file) {
        FileRequestDto thumbNailImageDto = service.upload(file);

        String thumbNailImageDtoImageUrl = thumbNailImageDto.getImageUrl();

        Channel channel = new Channel(
                requestDto,
                user,
                thumbNailImageDtoImageUrl
        );
         Channel channel1 = channelRepository.save(channel);

        chatRoomRepository.createChatRoom(channel1);
    }

    public Object readPost(User user) {
        LiveChannelResponse liveChannelResponse = new LiveChannelResponse();
        List<LiveChannelResponseDto> liveChannelResponseDtoList = new ArrayList<>();

        List<Channel> postList = channelRepository.findAll();
        for (Channel postDto : postList) {
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

    public Object readPostDetail(User user, Long videoChatPostId) {
        Channel channel = channelRepository.findById(videoChatPostId)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_VIDEOCHATPOST));

        chatRoomRepository.enterChatRoom(String.valueOf(videoChatPostId));

        return null;
    }
}
